package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.folder.FolderOrderOption
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderSummariesUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.PutFolderOrderUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.folder.model.FolderOrderUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderOrderViewModel @Inject constructor(
    private val putFolderOrderUseCase: PutFolderOrderUseCase,
    private val getFolderSummariesUseCase: GetFolderSummariesUseCase,
) : BaseViewModel() {
    private val _uiModel = MutableStateFlow(FolderOrderUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        getFolders()
    }

    private fun getFolders() {
        if (uiModel.value.fetchState is WishBoardState.Loading) return
        _uiModel.update { it.copy(fetchState = WishBoardState.Loading) }

        viewModelScope.launch {
            getFolderSummariesUseCase(orderOption = FolderOrderOption.CUSTOM).onSuccess { folders ->
                _uiModel.update {
                    it.copy(
                        originFolders = folders,
                        customFolders = folders,
                        fetchState = WishBoardState.Success(Unit),
                    )
                }
            }.onFailure { exception, errorCode, _ ->
                when (errorCode) {
                    // TODO
//                    404 -> {
//                        folderUiModel.update {
//                            it.copy(folders = emptyList(), fetchState = WishBoardState.Success(Unit))
//                        }
//                        afterSuccess(emptyList())
//                    }

                    else -> {
                        updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                        _uiModel.update {
                            it.copy(fetchState = WishBoardState.Failure)
                        }
                    }
                }
            }
        }
    }

    fun saveReorderedFolder(afterSuccess: () -> Unit) {
        if (uiModel.value.saveState is WishBoardState.Loading) return
        _uiModel.update { it.copy(saveState = WishBoardState.Loading) }

        viewModelScope.launch {
            putFolderOrderUseCase(uiModel.value.customFolders.map { it.id }).onSuccess {
                _uiModel.update {
                    it.copy(saveState = WishBoardState.Success(Unit))
                }
                updateSnackbarMessage(message = "폴더 정렬 방식을 변경했어요 📁")
                afterSuccess()
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                _uiModel.update {
                    it.copy(saveState = WishBoardState.Failure)
                }
            }
        }
    }
}
