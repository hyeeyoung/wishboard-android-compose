package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderDetailUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFolderDetailUseCase: GetFolderDetailUseCase,
) : BaseViewModel() {
    private val folderId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val wishList = folderId
        .filterNotNull()
        .flatMapLatest { id ->
            getFolderDetailUseCase(id)
                .catch { exception ->
                    updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                    emit(PagingData.empty())
                }
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())
    private val _refreshFolderDetailTrigger = Channel<Unit>()
    val refreshFolderDetailTrigger = _refreshFolderDetailTrigger.receiveAsFlow()

    init {
        val id = savedStateHandle.get<Long>(MainScreen.FolderDetail.ARG_FOLDER_ID)
        id?.let { folderId.update { id } }

        refreshFolderDetail()
    }

    private fun refreshFolderDetail() {
        viewModelScope.launch {
            WishBoardEventBus.onWishItemChanged.collect {
                _refreshFolderDetailTrigger.send(Unit)
            }
        }
    }
}
