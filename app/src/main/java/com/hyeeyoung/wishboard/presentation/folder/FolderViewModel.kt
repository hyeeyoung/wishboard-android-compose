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
            }.onFailure { exception, errorCode, _ ->
                when (errorCode) {
                    404 -> {
                        _uiModel.update {
                            it.copy(
                                folders = emptyList(),
                                fetchState = WishBoardState.Success(Unit),
                                isRefreshing = false,
                            )
                        }
                    }

                    else -> {
                        updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                        _uiModel.update {
                            it.copy(fetchState = WishBoardState.Failure, isRefreshing = false)
                        }
                    }
                }
            }
        }
    }

    fun createFolder(folderName: String, afterSuccess: () -> Unit) {
        if (uiModel.value.addState is WishBoardState.Loading) return

        val trimmedName = folderName.trim()
        _uiModel.update { it.copy(addState = WishBoardState.Loading) }

        viewModelScope.launch {
            postNewFolderUseCase(trimmedName)
                .onSuccess {
                    _uiModel.update { it.copy(addState = WishBoardState.Success(Unit), existingFolderName = "") }
                    getFolders(false)
                    afterSuccess()
                    updateSnackbarMessage("폴더를 추가했어요!😉")
                }.onFailure { exception, errorCode, _ ->
                    when (errorCode) {
                        409 -> _uiModel.update { it.copy(existingFolderName = trimmedName) }
                        else -> updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                    }
                    _uiModel.update { it.copy(addState = WishBoardState.Failure) }
                }
        }
    }

    fun updateFolder(folderId: Long, folderName: String, afterSuccess: () -> Unit) {
        if (uiModel.value.updateState is WishBoardState.Loading) return

        val trimmedName = folderName.trim()
        _uiModel.update { it.copy(updateState = WishBoardState.Loading) }

        viewModelScope.launch {
            putFolderNameUseCase(folderId = folderId, folderName = trimmedName).onSuccess {
                val folders = uiModel.value.folders.map { folder ->
                    if (folder.id == folderId) {
                        folder.copy(name = trimmedName)
                    } else {
                        folder
                    }
                }
                _uiModel.update {
                    it.copy(
                        folders = folders,
                        updateState = WishBoardState.Success(Unit),
                        existingFolderName = "",
                    )
                }
                afterSuccess()
                updateSnackbarMessage("폴더명을 수정했어요!📁")
            }.onFailure { exception, errorCode, _ ->
                when (errorCode) {
                    409 -> _uiModel.update { it.copy(existingFolderName = trimmedName) }
                    else -> updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                }
                _uiModel.update { it.copy(updateState = WishBoardState.Failure) }
            }
        }
    }

    fun deleteFolder(folderId: Long?) {
        if (folderId == null) {
            updateSnackbarMessage(SnackbarMessage.DEFAULT)
            return
        }

        if (uiModel.value.deleteState is WishBoardState.Loading) return

        _uiModel.update { it.copy(deleteState = WishBoardState.Loading) }

        viewModelScope.launch {
            deleteFolderUseCase(folderId = folderId).onSuccess {
                val folder = uiModel.value.folders.find { it.id == folderId } ?: return@launch
                val folders = uiModel.value.folders.minus(folder)
                _uiModel.update { it.copy(folders = folders, deleteState = WishBoardState.Success(Unit)) }
                updateSnackbarMessage("폴더를 삭제했어요!🗑️")
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                _uiModel.update { it.copy(deleteState = WishBoardState.Failure) }
            }
        }
    }

    fun getFolderDetail(folderId: Long) {
        viewModelScope.launch {
            getFolderDetailUseCase(folderId).onSuccess { items ->
                _detailUiModel.update { items }
            }.onFailure { exception, errorCode, _ ->
                when (errorCode) {
                    404 -> {
                        _detailUiModel.update { emptyList() }
                    }
                    else -> updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                }
            }
        }
    }

    fun clearModalData() {
        _uiModel.update {
            it.copy(
                addState = WishBoardState.Idle,
                updateState = WishBoardState.Idle,
                deleteState = WishBoardState.Idle,
                existingFolderName = null,
            )
        }
    }
}
