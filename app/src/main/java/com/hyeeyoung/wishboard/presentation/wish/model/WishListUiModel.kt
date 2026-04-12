package com.hyeeyoung.wishboard.presentation.wish.model

data class WishListUiModel(
    val shouldShowOnboardingModal: Boolean = false,
    val viewType: WishListViewType = WishListViewType.GRID_2_COLUMN,
)
