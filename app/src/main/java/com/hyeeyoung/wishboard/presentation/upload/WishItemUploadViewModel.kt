package com.hyeeyoung.wishboard.presentation.upload

import android.content.Context
import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderSummariesUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.PostNewFolderUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.GetParsedItemInfoUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PostWishItemUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PutWishItemUseCase
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.toLocalDateTime
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.WishBoardSnackbarVisuals
import com.hyeeyoung.wishboard.presentation.upload.model.ManualUploadItemUiModel
import com.hyeeyoung.wishboard.presentation.upload.model.UploadImage
import com.hyeeyoung.wishboard.presentation.upload.model.ParsingUploadItemUiModel
import com.hyeeyoung.wishboard.presentation.util.extension.BitmapUtil.toBitmap
import com.hyeeyoung.wishboard.presentation.util.extension.BitmapUtil.toFile
import com.hyeeyoung.wishboard.presentation.util.extension.convertResizeImage
import com.hyeeyoung.wishboard.presentation.util.extension.getBase64Json
import com.hyeeyoung.wishboard.presentation.util.extension.getValidUrl
import com.hyeeyoung.wishboard.presentation.util.extension.makeValidPriceStr
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
import kotlinx.datetime.toJavaLocalDateTime
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
    private val getFolderSummariesUseCase: GetFolderSummariesUseCase,
    private val getParsedItemInfoUseCase: GetParsedItemInfoUseCase,
    private val postNewFolderUseCase: PostNewFolderUseCase,
) : BaseViewModel() {
    private var _parsingUiModel = MutableStateFlow(ParsingUploadItemUiModel())
    val parsingUiModel = _parsingUiModel.asStateFlow()

    private var _manualUploadUiModel = MutableStateFlow(ManualUploadItemUiModel())
    val manualUploadUiModel = _manualUploadUiModel.asStateFlow()

    init {
        val detail = savedStateHandle.getBase64Json<WishItemDetail>(MainScreen.Upload.ARG_ITEM_DETAIL)
        detail?.let {
            setWishItemUploadModel(it)
        }
    }

    fun getParsedWishItem(site: String) {
        _parsingUiModel.update { it.copy(isLogin = localStorage.isLogin) }
        if (!localStorage.isLogin) {
            return
        }

        val itemSite = site.getValidUrl() ?: ""
        viewModelScope.launch {
            getParsedItemInfoUseCase(itemSite).onSuccess { parsedItem ->
                _parsingUiModel.update {
                    it.copy(
                        itemName = TextFieldValue(parsedItem.name ?: ""),
                        itemPrice = TextFieldValue(parsedItem.price ?: ""),
                        downloadImageUrl = parsedItem.image,
                        itemUrl = itemSite,
                    )
                }
            }.onFailure { _, _, _ ->
                _parsingUiModel.update {
                    it.copy(
                        itemUrl = itemSite,
                    )
                }
                updateSnackbarMessage("앗, 아이템 정보를 불러오지 못했어요🥲")
            }
        }
    }

    fun uploadWishItemForParsing(
        context: Context,
        afterSuccess: (Long) -> Unit,
    ) {
        if (parsingUiModel.value.wishItemUploadState is WishBoardState.Loading ||
            parsingUiModel.value.wishItemUploadState is WishBoardState.Success
        ) {
            return
        }

        _parsingUiModel.update { it.copy(wishItemUploadState = WishBoardState.Loading) }

        viewModelScope.launch {
            val bitmap = parsingUiModel.value.downloadImageUrl?.toBitmap()
            val file = bitmap?.toFile(localStorage.accessToken, context = context)
            val image = file?.let { ImageType.DownloadImage(file = it) }

            postWishItemUseCase(
                uploadType = WishItemUploadType.PARSING,
                itemInfo = parsingUiModel.value.toDomain(
                    itemImage = image,
                    uploadType = WishItemUploadType.PARSING,
                ),
            ).onSuccess { id ->
                _parsingUiModel.update { it.copy(wishItemUploadState = WishBoardState.Success(Unit)) }
                updateSnackbarMessage(ITEM_UPLOAD_SUCCESS_MESSAGE)
                delay(SnackbarDuration.Short.toMillis())
                afterSuccess(id)
            }.onFailure { exception, _, _ ->
                _parsingUiModel.update { it.copy(wishItemUploadState = WishBoardState.Failure) }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun uploadWishItemForManual(
        context: Context,
        afterSuccess: (Long) -> Unit,
    ) {
        if (_manualUploadUiModel.value.wishItemUploadState is WishBoardState.Loading ||
            _manualUploadUiModel.value.wishItemUploadState is WishBoardState.Success
        ) {
            return
        }

        _manualUploadUiModel.update { it.copy(wishItemUploadState = WishBoardState.Loading) }

        viewModelScope.launch {
            postWishItemUseCase(
                uploadType = WishItemUploadType.MANUAL,
                itemInfo = _manualUploadUiModel.value.toDomain(
                    itemImage = _manualUploadUiModel.value.images.toImageType(
                        context = context,
                    ),
                ),
            ).onSuccess { id ->
                _manualUploadUiModel.update { it.copy(wishItemUploadState = WishBoardState.Success(Unit)) }
                updateSnackbarMessage(ITEM_UPLOAD_SUCCESS_MESSAGE)

                afterSuccess(id)
            }.onFailure { exception, _, _ ->
                _manualUploadUiModel.update { it.copy(wishItemUploadState = WishBoardState.Failure) }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun updateWishItem(
        context: Context,
        itemId: Long?,
        afterSuccess: () -> Unit,
    ) {
        if (itemId == null) {
            updateSnackbarMessage(SnackbarMessage.DEFAULT)
            return
        }

        if (parsingUiModel.value.wishItemUploadState is WishBoardState.Loading) return
        _parsingUiModel.update { it.copy(wishItemUploadState = WishBoardState.Loading) }

        viewModelScope.launch {
            val image = when (parsingUiModel.value.itemImageUri) {
                null -> null
                else -> parsingUiModel.value.itemImageUri?.let {
                    val file = parsingUiModel.value.itemImageUri?.let { uri ->
                        context.convertResizeImage(uri)
                    }
                    val requestBody = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    requestBody?.let {
                        ImageType.Picture(
                            MultipartBody.Part.createFormData("itemImages", file.name, requestBody),
                        )
                    }
                }
            }

            putWishItemUseCase(
                itemId = itemId,
                itemInfo = parsingUiModel.value.toDomain(itemImage = image, uploadType = WishItemUploadType.MANUAL),
            ).onSuccess {
                _parsingUiModel.update { it.copy(wishItemUploadState = WishBoardState.Success(Unit)) }
                updateSnackbarMessage("아이템을 수정했어요!✍️")
                afterSuccess()
            }.onFailure { exception, _, _ ->
                _parsingUiModel.update { it.copy(wishItemUploadState = WishBoardState.Failure) }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    private fun setWishItemUploadModel(itemDetail: WishItemDetail) {
        _manualUploadUiModel.update {
            it.copy(
                itemName = TextFieldValue(itemDetail.name),
                itemPrice = TextFieldValue(itemDetail.price),
                itemMemo = TextFieldValue(itemDetail.memo ?: ""),
                itemUrl = TextFieldValue(itemDetail.site ?: ""),
                itemNotiType = itemDetail.notiType,
                itemNotiDate = itemDetail.notiDate?.toLocalDateTime(WishBoardDateFormat.YYYY_MM_DD_T_HH_MM),
                images = itemDetail.image?.map { UploadImage.Remote(it) } ?: emptyList(),
                selectedFolder = safeLet(
                    itemDetail.folderId,
                    itemDetail.folderName,
                ) { id, name -> FolderItem(id = id, name = name) },
            )
        }
    }

    fun getFoldersNew() {
//        if (!localStorage.isLogin) {
//            return
//        }
        if (parsingUiModel.value.folderFetchState is WishBoardState.Loading) return
        _manualUploadUiModel.update { it.copy(folderFetchState = WishBoardState.Loading) }

        viewModelScope.launch {
            getFolderSummariesUseCase().onSuccess { folders ->
                _manualUploadUiModel.update {
                    it.copy(folders = folders, folderFetchState = WishBoardState.Success(Unit))
                }
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                _manualUploadUiModel.update {
                    it.copy(folderFetchState = WishBoardState.Failure)
                }
            }
        }
    }

    fun createFolderNew(folderName: String, afterSuccess: () -> Unit) {
        if (parsingUiModel.value.folderAddState is WishBoardState.Loading) return
        _manualUploadUiModel.update { it.copy(folderAddState = WishBoardState.Loading) }

        val trimmedName = folderName.trim()
        viewModelScope.launch {
            postNewFolderUseCase(trimmedName)
                .onSuccess { folder ->
                    _manualUploadUiModel.update {
                        it.copy(
                            folderAddState = WishBoardState.Success(Unit),
                            existingFolderName = "",
                            folders = listOf(folder) + it.folders,
                        )
                    }

                    updateSelectedFolder(folder)

                    afterSuccess()
                }.onFailure { exception, errorCode, _ ->
                    when (errorCode) {
                        400 -> _manualUploadUiModel.update { it.copy(existingFolderName = trimmedName) }
                        else -> updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                    }
                    _manualUploadUiModel.update { it.copy(folderAddState = WishBoardState.Failure) }
                }
        }
    }

    fun getFolders(afterSuccess: (List<FolderItem>) -> Unit) {
        if (!localStorage.isLogin) {
            return
        }
        if (parsingUiModel.value.folderFetchState is WishBoardState.Loading) return
        _parsingUiModel.update { it.copy(folderFetchState = WishBoardState.Loading) }

        viewModelScope.launch {
            getFolderSummariesUseCase().onSuccess { folders ->
                _parsingUiModel.update {
                    it.copy(folders = folders, folderFetchState = WishBoardState.Success(Unit))
                }
                afterSuccess(folders)
            }.onFailure { exception, errorCode, _ ->
                when (errorCode) {
                    404 -> {
                        _parsingUiModel.update {
                            it.copy(folders = emptyList(), folderFetchState = WishBoardState.Success(Unit))
                        }
                        afterSuccess(emptyList())
                    }

                    else -> {
                        updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                        _parsingUiModel.update {
                            it.copy(folderFetchState = WishBoardState.Failure)
                        }
                    }
                }
            }
        }
    }

    fun createFolder(folderName: String, afterSuccess: () -> Unit) {
        if (parsingUiModel.value.folderAddState is WishBoardState.Loading) return
        _parsingUiModel.update { it.copy(folderAddState = WishBoardState.Loading) }

        val trimmedName = folderName.trim()
        viewModelScope.launch {
            postNewFolderUseCase(trimmedName)
                .onSuccess {
                    _parsingUiModel.update {
                        it.copy(
                            folderAddState = WishBoardState.Success(Unit),
                            existingFolderName = "",
                        )
                    }
                    getFolders { folders ->
                        updateSelectedFolder(folders.firstOrNull())
                    }
                    afterSuccess()
                }.onFailure { exception, errorCode, _ ->
                    when (errorCode) {
                        409 -> _parsingUiModel.update { it.copy(existingFolderName = trimmedName) }
                        else -> updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                    }
                    _parsingUiModel.update { it.copy(folderAddState = WishBoardState.Failure) }
                }
        }
    }

    fun isValidNotiDate(notiInfo: NotiInfo, uploadType: WishItemUploadType): Boolean {
        val date = notiInfo.notiDate?.toJavaLocalDateTime() ?: return true
        val isInvalid = !date.isAfter(java.time.LocalDateTime.now())
        return if (isInvalid) {
            updateSnackbarMessage("현재 시간 이후로만 선택할 수 있어요")
            false
        } else {
            setNotiInfo(notiInfo = notiInfo, uploadType = uploadType)
            true
        }
    }

    fun onItemNameChanged(name: TextFieldValue, uploadType: WishItemUploadType) {
        when (uploadType) {
            WishItemUploadType.PARSING -> {
                _parsingUiModel.update {
                    it.copy(itemName = name)
                }
            }

            WishItemUploadType.MANUAL -> {
                _manualUploadUiModel.update {
                    it.copy(itemName = name)
                }
            }
        }
    }

    fun onItemPriceChanged(price: TextFieldValue, uploadType: WishItemUploadType) {
        val refinedPrice = price.copy(text = price.text.makeValidPriceStr() ?: "")

        when (uploadType) {
            WishItemUploadType.PARSING -> {
                _parsingUiModel.update {
                    it.copy(itemPrice = refinedPrice)
                }
            }

            WishItemUploadType.MANUAL -> {
                _manualUploadUiModel.update {
                    it.copy(itemPrice = refinedPrice)
                }
            }
        }
    }

    fun onItemMemoChanged(memo: TextFieldValue) {
        _manualUploadUiModel.update {
            it.copy(itemMemo = memo)
        }
    }

    fun updateSelectedFolder(folderItem: FolderItem?) {
        _manualUploadUiModel.update {
            it.copy(
                selectedFolder =
                if (folderItem?.id != _manualUploadUiModel.value.selectedFolder?.id) {
                    folderItem
                } else {
                    null
                },
            )
        }
        // TODO
        _parsingUiModel.update {
            it.copy(
                selectedFolder =
                if (folderItem?.id != parsingUiModel.value.selectedFolder?.id) {
                    folderItem
                } else {
                    null
                },
            )
        }
    }

    fun setItemUrl(url: TextFieldValue) {
        _manualUploadUiModel.update {
            it.copy(itemUrl = url)
        }
    }

    fun addItemImageUrl(uris: List<Uri>) {
        _manualUploadUiModel.update {
            it.copy(images = it.images + uris.map { uri -> UploadImage.Local(uri) })
        }
    }

    fun setNotiInfo(notiInfo: NotiInfo, uploadType: WishItemUploadType) {
        when (uploadType) {
            WishItemUploadType.MANUAL -> {
                _manualUploadUiModel.update {
                    it.copy(itemNotiType = notiInfo.notiType, itemNotiDate = notiInfo.notiDate)
                }
            }

            WishItemUploadType.PARSING -> {
                _parsingUiModel.update {
                    it.copy(itemNotiType = notiInfo.notiType, itemNotiDate = notiInfo.notiDate)
                }
            }
        }
    }

    fun setTokenForProfileImageUri() {
        _parsingUiModel.update {
            it.copy(accessToken = localStorage.accessToken)
        }
        _manualUploadUiModel.update {
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

    private suspend fun List<UploadImage>.toImageType(context: Context): List<ImageType> {
        return this.map {
            when (it) {
                is UploadImage.Local -> {
                    val file = it.uri.let { uri ->
                        context.convertResizeImage(uri)
                    }
                    val requestBody = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    requestBody?.let {
                        ImageType.Picture(
                            MultipartBody.Part.createFormData("itemImages", file.name, requestBody),
                        )
                    }
                }

                is UploadImage.Remote -> {
                    val bitmap = it.url.toBitmap()
                    val file = bitmap?.toFile(localStorage.accessToken, context = context)
                    file?.let { ImageType.DownloadImage(file = it) }
                }
            }
        }.filterNotNull()
    }

    fun deleteImage(id: String) {
        _manualUploadUiModel.update {
            it.copy(images = it.images.filter { it.id != id })
        }
    }

    companion object {
        private const val ITEM_UPLOAD_SUCCESS_MESSAGE = "아이템을 위시리스트에 추가했어요!👜"
    }
}
