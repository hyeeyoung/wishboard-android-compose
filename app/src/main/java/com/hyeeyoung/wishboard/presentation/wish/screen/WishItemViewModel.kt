package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.folder.FolderOrderOption
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderSummariesUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.DeleteWishItemUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishItemDetailUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PutFolderOfWishItemUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PutWishItemOwnershipUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PutWishItemUseCase
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.toUtcFormattedString
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.folder.model.FolderListUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import com.hyeeyoung.wishboard.presentation.wish.model.WishItemDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWishItemUseCase: GetWishItemDetailUseCase,
    private val getFolderSummariesUseCase: GetFolderSummariesUseCase,
    private val putFolderOfWishItemUseCase: PutFolderOfWishItemUseCase,
    private val deleteWishItemUseCase: DeleteWishItemUseCase,
    private val putWishItemOwnershipUseCase: PutWishItemOwnershipUseCase,
    private val putWishItemUseCase: PutWishItemUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishItemDetailUiModel())
    val uiModel = _uiModel.asStateFlow()

    private val folderUiModel = MutableStateFlow(FolderListUiModel())

    init {
        val id = savedStateHandle.get<Long>(MainScreen.WishItemDetail.ARG_WISH_ITEM_ID)
        id?.let {
            getWishItemDetail(id)
        }
    }

    private fun getWishItemDetail(id: Long) {
        viewModelScope.launch {
            getWishItemUseCase(id).onSuccess { detail ->
                _uiModel.update {
                    WishItemDetailUiModel.fromDomain(detail)
                }
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun getFolders(afterSuccess: (List<FolderItem>) -> Unit) {
        if (folderUiModel.value.fetchState is WishBoardState.Loading) return
        folderUiModel.update { it.copy(fetchState = WishBoardState.Loading) }

        viewModelScope.launch {
            getFolderSummariesUseCase(orderOption = FolderOrderOption.CUSTOM).onSuccess { folders ->
                folderUiModel.update {
                    it.copy(folders = folders, fetchState = WishBoardState.Success(Unit))
                }
                afterSuccess(folders)
            }.onFailure { exception, errorCode, _ ->
                when (errorCode) {
                    404 -> {
                        folderUiModel.update {
                            it.copy(folders = emptyList(), fetchState = WishBoardState.Success(Unit))
                        }
                        afterSuccess(emptyList())
                    }

                    else -> {
                        updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                        folderUiModel.update {
                            it.copy(fetchState = WishBoardState.Failure)
                        }
                    }
                }
            }
        }
    }

    fun updateFolder(folder: FolderItem) {
        viewModelScope.launch {
            putFolderOfWishItemUseCase(itemId = uiModel.value.id, folderId = folder.id).onSuccess {
                WishBoardEventBus.notifyWishItemChanged()
                _uiModel.update {
                    it.copy(folderId = folder.id, folderName = folder.name)
                }
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun deleteWishItem(itemId: Long?, afterSuccess: () -> Unit) {
        if (itemId == null) {
            updateSnackbarMessage(SnackbarMessage.DEFAULT)
            return
        }

        viewModelScope.launch {
            deleteWishItemUseCase(itemId).onSuccess {
                updateSnackbarMessage("아이템을 위시리스트에서 삭제했어요!🗑️")
                afterSuccess()
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun updateItemOwnership() {
        viewModelScope.launch {
            putWishItemOwnershipUseCase(
                itemId = uiModel.value.id,
                isOwnedItem = !uiModel.value.isOwnedItem,
            ).onSuccess { isOwnedItem ->
                val message = if (isOwnedItem) "소장템으로 바꿨어요! 👜" else "소장템에서 제거했어요!"
                updateSnackbarMessage(message)
                _uiModel.update { it.copy(isOwnedItem = isOwnedItem) }
                WishBoardEventBus.notifyWishItemChanged()
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun startEditingMemo() {
        _uiModel.update { it.copy(isEditingMemo = true, memoInput = it.memo.orEmpty()) }
    }

    fun onMemoInputChanged(value: String) {
        _uiModel.update { it.copy(memoInput = value) }
    }

    // 메모만 변경하는 경우에도 updateWishItem은 아이템 전체 정보를 요구하므로,
    // 나머지 필드는 현재 화면에 표시된 값을 그대로 채우고 이미지는 건드리지 않는다.
    //
    // 낙관적 업데이트: 응답을 기다리지 않고 화면을 바로 갱신하며, 성공/실패 모두 토스트를 띄우지 않는다.
    fun saveMemo() {
        val model = uiModel.value
        val trimmedMemo = model.memoInput.trim().ifEmpty { null }

        _uiModel.update { it.copy(memo = trimmedMemo, isEditingMemo = false) }

        viewModelScope.launch {
            putWishItemUseCase(
                itemId = model.id,
                itemInfo = WishItemUploadInfo(
                    folderId = model.folderId,
                    itemName = model.name,
                    itemPrice = model.price.toInt(),
                    itemUrl = model.site,
                    itemNotiType = model.notiType,
                    itemNotiDate = model.notiDate?.toUtcFormattedString(WishBoardDateFormat.YYYY_MM_DD_HH_MM_SS),
                    itemImage = null,
                    itemMemo = trimmedMemo,
                    updateInfo = WishItemUploadInfo.UpdateInfo(version = model.version, imageChanged = false),
                ),
            ).onSuccess {
                WishBoardEventBus.notifyWishItemChanged()
                // version이 서버에서 올라가므로, 다음 수정을 위해 최신 상태로 다시 받아온다.
                getWishItemDetail(model.id)
            }
        }
    }
}
