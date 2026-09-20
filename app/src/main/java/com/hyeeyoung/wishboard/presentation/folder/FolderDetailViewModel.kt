package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderDetailUseCase
import com.hyeeyoung.wishboard.domain.usecase.folder.GetFolderItemTotalCountUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.DeleteBulkWishItemsUseCase
import com.hyeeyoung.wishboard.domain.util.resolveBulkDeleteTarget
import com.hyeeyoung.wishboard.domain.util.safeValueOf
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val localStorage: WishBoardPreference,
    getFolderDetailUseCase: GetFolderDetailUseCase,
    getFolderItemTotalCountUseCase: GetFolderItemTotalCountUseCase,
    private val deleteBulkWishItemsUseCase: DeleteBulkWishItemsUseCase,
) : BaseViewModel() {
    val totalItemCount = getFolderItemTotalCountUseCase()

    private val folderId = MutableStateFlow<Long?>(null)

    private val _isExcludeOwnedItems = MutableStateFlow(false)
    val isExcludeOwnedItems = _isExcludeOwnedItems.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val wishList = combine(folderId.filterNotNull(), _isExcludeOwnedItems) { id, isExclude -> id to isExclude }
        .distinctUntilChanged()
        .flatMapLatest { (id, isExclude) ->
            getFolderDetailUseCase(id, itemStatus = if (isExclude) WishItemOwnershipStatus.WISH else null)
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

    private val _isAllSelected = MutableStateFlow(false)
    val isAllSelected = _isAllSelected.asStateFlow()

    private val _selectedItemIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedItemIds = _selectedItemIds.asStateFlow()

    private val _deleteSelectedItemsState = MutableStateFlow<WishBoardState<Unit>>(WishBoardState.Idle)
    val deleteSelectedItemsState = _deleteSelectedItemsState.asStateFlow()

    private val _viewType = MutableStateFlow(
        safeValueOf<WishListViewType>(localStorage.folderDetailViewType) ?: WishListViewType.GRID_2_COLUMN,
    )
    val viewType = _viewType.asStateFlow()

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
        _isAllSelected.update { false }
        _selectedItemIds.update { emptySet() }
    }

    // 전체 선택 상태에서는 개별 아이템을 부분적으로 해제하는 것을 지원하지 않는다 (다시 누르면 전체 해제).
    fun toggleSelectAll() {
        _isAllSelected.update { !it }
        _selectedItemIds.update { emptySet() }
    }

    fun toggleItemSelection(itemId: Long) {
        if (_isAllSelected.value) return

        _selectedItemIds.update {
            if (it.contains(itemId)) it - itemId else it + itemId
        }
    }

    fun setItemSelected(itemId: Long, isSelected: Boolean) {
        if (_isAllSelected.value) return

        _selectedItemIds.update { if (isSelected) it + itemId else it - itemId }
    }

    fun deleteSelectedItems(allLoadedItemIds: List<Long>) {
        val isExcludeOwned = _isExcludeOwnedItems.value
        val currentFolderId = folderId.value
        val target = resolveBulkDeleteTarget(
            isAllSelected = _isAllSelected.value,
            selectedItemIds = _selectedItemIds.value,
            allLoadedItemIds = allLoadedItemIds,
            totalItemCount = totalItemCount.value,
        )

        _deleteSelectedItemsState.update { WishBoardState.Loading }

        viewModelScope.launch {
            val result = deleteBulkWishItemsUseCase(
                scope = target.scope,
                folderId = currentFolderId,
                itemStatus = if (isExcludeOwned) WishItemOwnershipStatus.WISH else null,
                itemIds = target.itemIds,
                excludeItemIds = target.excludeItemIds,
            )

            _deleteSelectedItemsState.update { WishBoardState.Idle }
            _isSelectionMode.update { false }
            _isAllSelected.update { false }
            _selectedItemIds.update { emptySet() }

            result.onSuccess {
                updateSnackbarMessage("아이템을 위시리스트에서 삭제했어요!🗑")
                _refreshFolderDetailTrigger.send(Unit)
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(
                    message = "일시적인 오류가 발생했어요!\n잠시후 다시 시도해주세요",
                    exception = exception,
                )
            }
        }
    }

    fun updateViewType() {
        val newViewType = _viewType.value.next()
        _viewType.update { newViewType }
        localStorage.folderDetailViewType = newViewType.name
    }

    fun updateExcludeOwnedItems(isExclude: Boolean) {
        _isExcludeOwnedItems.update { isExclude }
    }
}
