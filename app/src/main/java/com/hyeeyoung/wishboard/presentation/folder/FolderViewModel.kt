package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderDetailUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFoldersUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.folder.model.FolderTabUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val getFoldersUseCase: GetFoldersUseCase,
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

    fun getFolderDetail(folderId: Long) {
        viewModelScope.launch {
            getFolderDetailUseCase(folderId).onSuccess { items ->
                _detailUiModel.update { items }
            }.onFailure { _, _,_ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }
}
