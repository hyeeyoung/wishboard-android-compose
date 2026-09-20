package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderDetailUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import com.hyeeyoung.wishboard.presentation.wish.model.WishListViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
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

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    private val _selectedItemIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedItemIds = _selectedItemIds.asStateFlow()

    private val _deleteSelectedItemsState = MutableStateFlow<WishBoardState<Unit>>(WishBoardState.Idle)
    val deleteSelectedItemsState = _deleteSelectedItemsState.asStateFlow()

    // TODO: 폴더 내 아이템 개수/소장템 필터는 서버 API 연동 후 실제 데이터로 교체
    private val _viewType = MutableStateFlow(WishListViewType.GRID_2_COLUMN)
    val viewType = _viewType.asStateFlow()

    private val _isExcludeOwnedItems = MutableStateFlow(false)
    val isExcludeOwnedItems = _isExcludeOwnedItems.asStateFlow()

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

    fun toggleSelectionMode() {
        _isSelectionMode.update { !it }
        _selectedItemIds.update { emptySet() }
    }

    fun toggleItemSelection(itemId: Long) {
        _selectedItemIds.update {
            if (it.contains(itemId)) it - itemId else it + itemId
        }
    }

    fun setItemSelected(itemId: Long, isSelected: Boolean) {
        _selectedItemIds.update { if (isSelected) it + itemId else it - itemId }
    }

    fun deleteSelectedItems() {
        _deleteSelectedItemsState.update { WishBoardState.Loading }
        // TODO: 선택된 아이템 일괄 삭제 API 연동
    }

    fun updateViewType() {
        _viewType.update { it.next() }
    }

    fun updateExcludeOwnedItems(isExclude: Boolean) {
        _isExcludeOwnedItems.update { isExclude }
    }
}
