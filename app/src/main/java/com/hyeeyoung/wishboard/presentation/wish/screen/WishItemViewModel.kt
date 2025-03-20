package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFoldersUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishItemDetailUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.PutFolderOfWishItemUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.folder.model.FolderListUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.wish.model.WishItemDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    private val getWishItemUseCase: GetWishItemDetailUseCase,
    private val getFoldersUseCase: GetFoldersUseCase,
    private val putFolderOfWishItemUseCase: PutFolderOfWishItemUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishItemDetailUiModel())
    val uiModel = _uiModel.asStateFlow()

    private val folderUiModel = MutableStateFlow(FolderListUiModel())

    fun getWishItemDetail(id: Long) {
        viewModelScope.launch {
            getWishItemUseCase(id).onSuccess {
                val detail = it.firstOrNull() ?: return@launch
                _uiModel.update {
                    WishItemDetailUiModel.fromDomain(detail)
                }
            }.onFailure { _, _, _ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }

    fun getFolders(afterSuccess: (List<FolderItem>) -> Unit) {
        if (folderUiModel.value.fetchState is WishBoardState.Loading) return
        folderUiModel.update { it.copy(fetchState = WishBoardState.Loading) }

        viewModelScope.launch {
            getFoldersUseCase().onSuccess { folders ->
                folderUiModel.update {
                    it.copy(folders = folders, fetchState = WishBoardState.Success(Unit))
                }
                afterSuccess(folders)
            }.onFailure { _, _, _ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
                folderUiModel.update {
                    it.copy(fetchState = WishBoardState.Failure)
                }
            }
        }
    }

    fun updateFolder(folder: FolderItem) {
        viewModelScope.launch {
            putFolderOfWishItemUseCase(itemId = uiModel.value.id, folderId = folder.id).onSuccess {
                _uiModel.update {
                    it.copy(folderId = folder.id, folderName = folder.name)
                }
            }.onFailure { _, _, _ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }
}
