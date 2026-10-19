package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.folder.FolderThumbnailImage
import com.hyeeyoung.wishboard.domain.usecase.folder.DeleteFolderThumbnailUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderThumbnailImagesUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.PutFolderThumbnailUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FolderThumbnailUiState(
    val images: List<FolderThumbnailImage> = emptyList(),
    val fetchState: WishBoardState<Unit> = WishBoardState.Loading,
    /** 진입 시점에 적용되어 있던 대표 사진의 itemImageId. 없으면 null */
    val initialSelectedImageId: Long? = null,
    val selectedImageId: Long? = null,
    val saveState: WishBoardState<Unit> = WishBoardState.Idle,
) {
    /** 진입 시점의 대표 사진과 비교해 선택이 바뀌었는지(다른 사진 선택, 선택 해제 모두 포함) */
    val hasChanges: Boolean
        get() = selectedImageId != initialSelectedImageId

    val isSaveEnabled: Boolean
        get() = fetchState is WishBoardState.Success && hasChanges && saveState !is WishBoardState.Loading

    val isEmpty: Boolean
        get() = fetchState is WishBoardState.Success && images.isEmpty()
}

@HiltViewModel
class FolderThumbnailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFolderThumbnailImagesUseCase: GetFolderThumbnailImagesUseCase,
    private val putFolderThumbnailUseCase: PutFolderThumbnailUseCase,
    private val deleteFolderThumbnailUseCase: DeleteFolderThumbnailUseCase,
) : BaseViewModel() {
    private val folderId: Long = checkNotNull(savedStateHandle.get<Long>(MainScreen.FolderThumbnail.ARG_FOLDER_ID))
    val folderName: String = savedStateHandle.get<String>(MainScreen.FolderThumbnail.ARG_FOLDER_NAME).orEmpty()

    // 폴더 탭에서 보이던 현재 썸네일 URL. 후보 중 URL이 같은 사진을 진입 시 선택된 상태로 둔다.
    private val currentThumbnailUrl: String =
        savedStateHandle.get<String>(MainScreen.FolderThumbnail.ARG_THUMBNAIL_URL).orEmpty()

    private val _uiState = MutableStateFlow(FolderThumbnailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchThumbnailImages()
    }

    private fun fetchThumbnailImages() {
        viewModelScope.launch {
            getFolderThumbnailImagesUseCase(folderId = folderId).onSuccess { images ->
                val currentImageId = if (currentThumbnailUrl.isBlank()) {
                    null
                } else {
                    images.firstOrNull { it.imageUrl == currentThumbnailUrl }?.itemImageId
                }
                _uiState.update {
                    it.copy(
                        images = images,
                        fetchState = WishBoardState.Success(Unit),
                        initialSelectedImageId = currentImageId,
                        selectedImageId = currentImageId,
                    )
                }
            }.onFailure { exception, _, _ ->
                _uiState.update { it.copy(fetchState = WishBoardState.Failure) }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    /** 하나만 선택할 수 있으며, 이미 선택된 사진을 다시 누르면 선택이 해제된다. */
    fun onClickImage(image: FolderThumbnailImage) {
        if (_uiState.value.saveState is WishBoardState.Loading) return
        _uiState.update {
            it.copy(selectedImageId = if (it.selectedImageId == image.itemImageId) null else image.itemImageId)
        }
    }

    fun save(afterSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.isSaveEnabled) return

        _uiState.update { it.copy(saveState = WishBoardState.Loading) }
        viewModelScope.launch {
            val selectedImageId = state.selectedImageId
            val result = if (selectedImageId == null) {
                deleteFolderThumbnailUseCase(folderId = folderId)
            } else {
                putFolderThumbnailUseCase(folderId = folderId, itemImageId = selectedImageId)
            }

            result.onSuccess {
                _uiState.update { it.copy(saveState = WishBoardState.Success(Unit)) }
                WishBoardEventBus.notifyFolderChanged()
                afterSuccess()
            }.onFailure { exception, _, _ ->
                _uiState.update { it.copy(saveState = WishBoardState.Failure) }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }
}
