package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.usecase.folder.DeleteFolderUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFoldersUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.PostNewFolderUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.PutFolderNameUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.folder.model.FolderTabUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    getFoldersUseCase: GetFoldersUseCase,
    private val postNewFolderUseCase: PostNewFolderUseCase,
    private val putFolderNameUseCase: PutFolderNameUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(FolderTabUiModel())
    val uiModel = _uiModel.asStateFlow()

    val folders = getFoldersUseCase() // TODO fetchState
        .cachedIn(viewModelScope)
        .catch { exception ->
            updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    private val _refreshFolderListTrigger = Channel<Unit>()
    val refreshFolderListTrigger = _refreshFolderListTrigger.receiveAsFlow()

    init {
        refreshFolders()
    }

    private fun refreshFolders() {
        viewModelScope.launch {
            WishBoardEventBus.onWishItemChanged.collect {
                _refreshFolderListTrigger.send(Unit)
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
                _uiModel.update {
                    it.copy(
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

    fun deleteFolder(folderId: Long?, afterSuccess: () -> Unit) {
        if (folderId == null) {
            updateSnackbarMessage(SnackbarMessage.DEFAULT)
            return
        }

        if (uiModel.value.deleteState is WishBoardState.Loading) return

        _uiModel.update { it.copy(deleteState = WishBoardState.Loading) }

        viewModelScope.launch {
            deleteFolderUseCase(folderId = folderId).onSuccess {
                afterSuccess()
                _uiModel.update { it.copy(deleteState = WishBoardState.Success(Unit)) }
                updateSnackbarMessage("폴더를 삭제했어요!🗑️")
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                _uiModel.update { it.copy(deleteState = WishBoardState.Failure) }
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
