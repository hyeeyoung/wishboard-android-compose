package com.hyeeyoung.wishboard.presentation.upload

import android.content.Context
import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFoldersUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.PostNewFolderUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.GetParsedItemInfoUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PostWishItemUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PutWishItemUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.WishBoardSnackbarVisuals
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemUploadUiModel
import com.hyeeyoung.wishboard.presentation.util.extension.BitmapUtil.toBitmap
import com.hyeeyoung.wishboard.presentation.util.extension.BitmapUtil.toFile
import com.hyeeyoung.wishboard.presentation.util.extension.convertResizeImage
import com.hyeeyoung.wishboard.presentation.util.extension.getBase64Json
import com.hyeeyoung.wishboard.presentation.util.extension.toMillis
import com.hyeeyoung.wishboard.presentation.util.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class WishItemUploadViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val localStorage: WishBoardPreference,
    private val postWishItemUseCase: PostWishItemUseCase,
    private val putWishItemUseCase: PutWishItemUseCase,
    private val getFoldersUseCase: GetFoldersUseCase,
    private val getParsedItemInfoUseCase: GetParsedItemInfoUseCase,
    private val postNewFolderUseCase: PostNewFolderUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishItemUploadUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        val detail = savedStateHandle.getBase64Json<WishItemDetail>(MainScreen.Upload.ARG_ITEM_DETAIL)
        detail?.let {
            setWishItemUploadModel(it)
        }
    }

    fun getParsedWishItem(site: String) {
        _uiModel.update { it.copy(isLogin = localStorage.isLogin) }

        viewModelScope.launch {
            getParsedItemInfoUseCase(site).onSuccess { parsedItem ->
                _uiModel.update {
                    it.copy(
                        itemName = parsedItem.name ?: "",
                        itemPrice = parsedItem.price ?: "",
                        downloadImageUrl = parsedItem.image,
                        itemUrl = site
                    )
                }
            }.onFailure { _, _, _ ->
                updateSnackbarMessage("앗, 아이템 정보를 불러오지 못했어요🥲")
            }
        }
    }

    fun uploadWishItem(
        context: Context,
        uploadType: WishItemUploadType,
        afterSuccess: (Long) -> Unit
    ) {
        if (uiModel.value.wishItemUploadState is WishBoardState.Loading) return
        _uiModel.update { it.copy(wishItemUploadState = WishBoardState.Loading) }

        viewModelScope.launch {
            val image = when (uploadType) {
                WishItemUploadType.MANUAL -> {
                    val file = uiModel.value.itemImageUri?.let { uri ->
                        context.convertResizeImage(uri)
                    }
                    val requestBody = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    requestBody?.let {
                        ImageType.Picture(
                            MultipartBody.Part.createFormData("item_img", file.name, requestBody)
                        )
                    }
                }

                WishItemUploadType.PARSING -> {
                    val bitmap = uiModel.value.downloadImageUrl?.toBitmap()
                    val file = bitmap?.toFile(localStorage.accessToken, context = context)
                    file?.let { ImageType.DownloadImage(file = it) }
                }
            }

            postWishItemUseCase(
                uploadType = uploadType,
                itemInfo = uiModel.value.toDomain(itemImage = image, uploadType = uploadType),
            ).onSuccess { id ->
                _uiModel.update { it.copy(wishItemUploadState = WishBoardState.Success(Unit)) }
                updateSnackbarMessage("아이템을 위시리스트에 추가했어요!👜")

                if (uploadType == WishItemUploadType.PARSING) {
                    delay(SnackbarDuration.Short.toMillis())
                }

                afterSuccess(id)
            }.onFailure { _, _, _ ->
                _uiModel.update { it.copy(wishItemUploadState = WishBoardState.Failure) }
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }

    fun updateWishItem(
        context: Context,
        itemId: Long?,
        afterSuccess: () -> Unit
    ) {
        if (itemId == null) {
            updateSnackbarMessage(SnackbarMessage.DEFAULT)
            return
        }

        viewModelScope.launch {
            val image = when (uiModel.value.itemImageUri) {
                null -> null
                else -> uiModel.value.itemImageUri?.let {
                    val file = uiModel.value.itemImageUri?.let { uri ->
                        context.convertResizeImage(uri)
                    }
                    val requestBody = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    requestBody?.let {
                        ImageType.Picture(
                            MultipartBody.Part.createFormData("item_img", file.name, requestBody)
                        )
                    }
                }
            }

            putWishItemUseCase(
                itemId = itemId,
                itemInfo = uiModel.value.toDomain(itemImage = image, uploadType = WishItemUploadType.MANUAL),
            ).onSuccess {
                updateSnackbarMessage("아이템을 수정했어요!✍️")
                afterSuccess()
            }.onFailure { _, _, _ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }

    private fun setWishItemUploadModel(itemDetail: WishItemDetail) {
        _uiModel.update {
            it.copy(
                itemName = itemDetail.name,
                itemPrice = itemDetail.price,
                itemMemo = itemDetail.memo ?: "",
                itemUrl = itemDetail.site ?: "",
                downloadImageUrl = itemDetail.image,
                selectedFolder = safeLet(
                    itemDetail.folderId,
                    itemDetail.folderName
                ) { id, name -> FolderItem(id = id, name = name) }
            )
        }
    }

    fun getFolders(afterSuccess: (List<FolderItem>) -> Unit) {
        if (uiModel.value.folderFetchState is WishBoardState.Loading) return

        _uiModel.update { it.copy(folderFetchState = WishBoardState.Loading) }

        viewModelScope.launch {
            getFoldersUseCase().onSuccess { folders ->
                _uiModel.update {
                    it.copy(folders = folders, folderFetchState = WishBoardState.Success(Unit))
                }
                afterSuccess(folders)
            }.onFailure { _, errorCode, _ ->
                when(errorCode) {
                    404 -> {
                        _uiModel.update {
                            it.copy(folderFetchState = WishBoardState.Success(Unit))
                        }
                        afterSuccess(emptyList())
                    }
                    else -> {
                        updateSnackbarMessage(SnackbarMessage.DEFAULT)
                        _uiModel.update {
                            it.copy(folderFetchState = WishBoardState.Failure)
                        }
                    }
                }
            }
        }
    }

    fun createFolder(folderName: String, afterSuccess: () -> Unit) {
        if (uiModel.value.folderAddState is WishBoardState.Loading) return

        val trimmedName = folderName.trim()
        _uiModel.update { it.copy(folderAddState = WishBoardState.Loading) }

        viewModelScope.launch {
            postNewFolderUseCase(trimmedName)
                .onSuccess {
                    _uiModel.update { it.copy(folderAddState = WishBoardState.Success(Unit), existingFolderName = "") }
                    getFolders { folders ->
                        updateSelectedFolder(folders.firstOrNull())
                    }
                    afterSuccess()
                }.onFailure { _, errorCode, _ ->
                    when (errorCode) {
                        409 -> _uiModel.update { it.copy(existingFolderName = trimmedName) }
                        else -> updateSnackbarMessage(SnackbarMessage.DEFAULT)
                    }
                    _uiModel.update { it.copy(folderAddState = WishBoardState.Failure) }
                }
        }
    }

    fun onItemNameChanged(name: String) {
        _uiModel.update {
            it.copy(itemName = name)
        }
    }

    fun onItemPriceChanged(price: String) {
        _uiModel.update {
            it.copy(itemPrice = price)
        }
    }

    fun onItemMemoChanged(memo: String) {
        _uiModel.update {
            it.copy(itemMemo = memo)
        }
    }

    fun updateSelectedFolder(folderItem: FolderItem?) {
        _uiModel.update {
            it.copy(selectedFolder = folderItem)
        }
    }

    fun setItemUri(url: String) {
        _uiModel.update {
            it.copy(itemUrl = url)
        }
    }

    fun setItemImageUrl(uri: Uri?) {
        _uiModel.update {
            it.copy(itemImageUri = uri)
        }
    }

    fun setNotiInfo(notiInfo: NotiInfo) {
        _uiModel.update {
            it.copy(itemNotiType = notiInfo.notiType, itemNotiDate = notiInfo.notiDate)
        }
    }

    fun setTokenForProfileImageUri() {
        _uiModel.update {
            it.copy(accessToken = localStorage.accessToken)
        }
    }

    /** 스낵바 시각 정보(전역으로 사용) */
    val globalSnackbarChannel = Channel<WishBoardSnackbarVisuals>(
        capacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun sendSnackbarChannel(snackbarVisuals: WishBoardSnackbarVisuals) {
        viewModelScope.launch {
            globalSnackbarChannel.send(snackbarVisuals)
        }
    }
}
