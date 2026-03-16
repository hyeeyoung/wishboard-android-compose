package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.folder.FolderOrderOption
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderSummariesUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.DeleteWishItemUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishItemDetailUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PutFolderOfWishItemUseCase
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
}
