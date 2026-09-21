package com.hyeeyoung.wishboard.presentation.wish

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.usecase.item.DeleteBulkWishItemsUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.GetTotalWishItemsUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishListUseCase
import com.hyeeyoung.wishboard.domain.util.resolveBulkDeleteTarget
import com.hyeeyoung.wishboard.domain.util.safeValueOf
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import com.hyeeyoung.wishboard.presentation.wish.model.WishListUiModel
import com.hyeeyoung.wishboard.presentation.wish.model.WishListViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val localStorage: WishBoardPreference,
    getWishListUseCase: GetWishListUseCase,
    getTotalWishItemsUseCase: GetTotalWishItemsUseCase,
    private val deleteBulkWishItemsUseCase: DeleteBulkWishItemsUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishListUiModel())
    val uiModel = _uiModel.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val wishList: Flow<PagingData<WishItem>> = _uiModel
        .map { it.isExcludeOwnedItems }
        .distinctUntilChanged()
        .flatMapLatest { isExclude ->
            getWishListUseCase(
                itemStatus = if (isExclude) WishItemOwnershipStatus.WISH else null,
            )
        }
        .cachedIn(viewModelScope)
        .catch { exception ->
            updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    private val _refreshWishListTrigger = Channel<Unit>()
    val refreshWishListTrigger = _refreshWishListTrigger.receiveAsFlow()

    private val _scrollToTopTrigger = Channel<Unit>()
    val scrollToTopTrigger = _scrollToTopTrigger.receiveAsFlow()

    init {
        initUiModel()
        refreshWishList()
        observeTotalItemCount(getTotalWishItemsUseCase)
    }

    private fun refreshWishList() {
        viewModelScope.launch {
            WishBoardEventBus.onWishItemChanged.collect {
                _refreshWishListTrigger.send(Unit)
            }
        }
    }

    private fun observeTotalItemCount(getTotalWishItemsUseCase: GetTotalWishItemsUseCase) {
        viewModelScope.launch {
            getTotalWishItemsUseCase().collect { total ->
                _uiModel.update { it.copy(totalItemCount = total) }
            }
        }
    }

    private fun initUiModel() {
        _uiModel.update {
            it.copy(
                shouldShowOnboardingModal = localStorage.shouldShowOnboardingModal,
                viewType = safeValueOf<WishListViewType>(localStorage.wishListViewType)
                    ?: WishListViewType.GRID_2_COLUMN,
                isBulkRegisterBannerVisible = !localStorage.isBulkRegisterBannerDismissed,
            )
        }
    }

    fun dismissBulkRegisterBanner() {
        localStorage.isBulkRegisterBannerDismissed = true
        _uiModel.update { it.copy(isBulkRegisterBannerVisible = false) }
        updateSnackbarMessage(message = "마이페이지 > ‘위시리스트 한 번에 등록하기’\n에서 언제든 확인할 수 있어요!")
    }

    fun updateOnboardingModalStatus(isOnboardingComplete: Boolean) {
        if (isOnboardingComplete) {
            localStorage.clear(WishBoardPreference.SHOULD_SHOW_ONBOARDING_MODAL)
        }

        _uiModel.update {
            it.copy(shouldShowOnboardingModal = false)
        }
    }

    fun updateViewType() {
        val newViewType = uiModel.value.viewType.next()
        _uiModel.update {
            it.copy(viewType = newViewType)
        }
        localStorage.wishListViewType = newViewType.name
    }

    fun updateExcludeOwnedItems(isExclude: Boolean) {
        _uiModel.update {
            it.copy(isExcludeOwnedItems = isExclude, isAllSelected = false, selectedItemIds = emptySet())
        }
        viewModelScope.launch { _scrollToTopTrigger.send(Unit) }
    }

    fun toggleSelectionMode() {
        _uiModel.update {
            it.copy(isSelectionMode = !it.isSelectionMode, isAllSelected = false, selectedItemIds = emptySet())
        }
    }

    // 전체 선택 상태에서는 개별 아이템을 부분적으로 해제하는 것을 지원하지 않는다 (다시 누르면 전체 해제).
    fun toggleSelectAll() {
        _uiModel.update { it.copy(isAllSelected = !it.isAllSelected, selectedItemIds = emptySet()) }
    }

    fun toggleItemSelection(itemId: Long) {
        _uiModel.update {
            if (it.isAllSelected) return@update it

            val selectedItemIds = if (it.selectedItemIds.contains(itemId)) {
                it.selectedItemIds - itemId
            } else {
                it.selectedItemIds + itemId
            }
            it.copy(selectedItemIds = selectedItemIds)
        }
    }

    fun setItemSelected(itemId: Long, isSelected: Boolean) {
        _uiModel.update {
            if (it.isAllSelected) return@update it

            val selectedItemIds = if (isSelected) it.selectedItemIds + itemId else it.selectedItemIds - itemId
            it.copy(selectedItemIds = selectedItemIds)
        }
    }

    fun deleteSelectedItems(allLoadedItemIds: List<Long>) {
        val model = uiModel.value
        val target = resolveBulkDeleteTarget(
            isAllSelected = model.isAllSelected,
            selectedItemIds = model.selectedItemIds,
            allLoadedItemIds = allLoadedItemIds,
            totalItemCount = model.totalItemCount,
        )

        _uiModel.update { it.copy(deleteSelectedItemsState = WishBoardState.Loading) }

        viewModelScope.launch {
            val result = deleteBulkWishItemsUseCase(
                scope = target.scope,
                itemStatus = if (model.isExcludeOwnedItems) WishItemOwnershipStatus.WISH else null,
                itemIds = target.itemIds,
                excludeItemIds = target.excludeItemIds,
            )

            _uiModel.update {
                it.copy(
                    deleteSelectedItemsState = WishBoardState.Idle,
                    isSelectionMode = false,
                    isAllSelected = false,
                    selectedItemIds = emptySet(),
                )
            }

            result.onSuccess {
                updateSnackbarMessage("아이템을 위시리스트에서 삭제했어요!🗑")
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(
                    message = "일시적인 오류가 발생했어요!\n잠시후 다시 시도해주세요",
                    exception = exception,
                )
            }

            // 500개 초과 시 여러 번에 나눠 삭제하므로, 실패해도 일부는 이미 삭제됐을 수 있어 항상 새로고침한다.
            _refreshWishListTrigger.send(Unit)
        }
    }
}
