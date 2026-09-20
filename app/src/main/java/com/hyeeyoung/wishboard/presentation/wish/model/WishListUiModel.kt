package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

data class WishListUiModel(
    val shouldShowOnboardingModal: Boolean = false,
    val viewType: WishListViewType = WishListViewType.GRID_2_COLUMN,
    val totalItemCount: Int? = null,
    val isExcludeOwnedItems: Boolean = false,
    val isBulkRegisterBannerVisible: Boolean = false,
    val isSelectionMode: Boolean = false,
    val isAllSelected: Boolean = false,
    val selectedItemIds: Set<Long> = emptySet(),
    val deleteSelectedItemsState: WishBoardState<Unit> = WishBoardState.Idle,
)
