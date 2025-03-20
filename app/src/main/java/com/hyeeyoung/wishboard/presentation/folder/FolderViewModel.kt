package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.usecase.folder.DeleteFolderUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderDetailUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFoldersUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.PostNewFolderUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.PutFolderNameUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.folder.model.FolderTabUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val getFoldersUseCase: GetFoldersUseCase,
    private val postNewFolderUseCase: PostNewFolderUseCase,
    private val putFolderNameUseCase: PutFolderNameUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val getFolderDetailUseCase: GetFolderDetailUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(FolderTabUiModel())
    val uiModel = _uiModel.asStateFlow()

    private var _detailUiModel = MutableStateFlow(emptyList<WishItem>())
    val detailUiModel = _detailUiModel.asStateFlow()

    fun getFolders(didRefresh: Boolean = false) {
        if (uiModel.value.fetchState is WishBoardState.Loading) return
        _uiModel.update { it.copy(fetchState = WishBoardState.Loading, isRefreshing = didRefresh) }

        viewModelScope.launch {
            getFoldersUseCase().onSuccess { folders ->
                _uiModel.update {
                    it.copy(folders = folders, fetchState = WishBoardState.Success(Unit), isRefreshing = false)
                }
            }.onFailure { _, _, _ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
                _uiModel.update {
                    it.copy(fetchState = WishBoardState.Failure, isRefreshing = false)
                }
            }
        }
    }

    fun createFolder(folderName: String) {
        if (uiModel.value.addState is WishBoardState.Loading) return

        val trimmedName = folderName.trim()
        _uiModel.update { it.copy(addState = WishBoardState.Loading) }

        viewModelScope.launch {
            postNewFolderUseCase(trimmedName)
                .onSuccess {
                    _uiModel.update { it.copy(addState = WishBoardState.Success(Unit), existingFolderName = "") }
                }.onFailure { _, errorCode, _ ->
                    when (errorCode) {
                        409 -> _uiModel.update { it.copy(existingFolderName = trimmedName) }
                        else -> updateSnackbarMessage(SnackbarMessage.DEFAULT)
                    }
                    _uiModel.update { it.copy(addState = WishBoardState.Failure) }
                }
        }
    }

    fun updateFolder(folderId: Long, folderName: String) {
        if (uiModel.value.updateState is WishBoardState.Loading) return

        val trimmedName = folderName.trim()
        _uiModel.update { it.copy(updateState = WishBoardState.Loading) }

        viewModelScope.launch {
            putFolderNameUseCase(folderId = folderId, folderName = trimmedName).onSuccess {
                _uiModel.update { it.copy(updateState = WishBoardState.Success(Unit), existingFolderName = "") }
            }.onFailure { _, errorCode, _ ->
                when (errorCode) {
                    409 -> _uiModel.update { it.copy(existingFolderName = trimmedName) }
                    else -> updateSnackbarMessage(SnackbarMessage.DEFAULT)
                }
                _uiModel.update { it.copy(updateState = WishBoardState.Failure) }
            }
        }
    }

    fun deleteFolder(folderId: Long) {
        if (uiModel.value.deleteState is WishBoardState.Loading) return

        _uiModel.update { it.copy(deleteState = WishBoardState.Loading) }

        viewModelScope.launch {
            deleteFolderUseCase(folderId = folderId).onSuccess {
                _uiModel.update { it.copy(deleteState = WishBoardState.Success(Unit)) }
            }.onFailure { _, _, _ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
                _uiModel.update { it.copy(deleteState = WishBoardState.Failure) }
            }
        }
    }

    fun getFolderDetail(folderId: Long) {
        viewModelScope.launch {
            getFolderDetailUseCase(folderId).onSuccess { items ->
                _detailUiModel.update { items }
            }.onFailure { _, _, _ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }

    fun clearDialogData() {
        _uiModel.update {
            it.copy(
                addState = WishBoardState.Idle,
                updateState = WishBoardState.Idle,
                deleteState = WishBoardState.Idle,
                existingFolderName = null
            )
        }
    }
}
