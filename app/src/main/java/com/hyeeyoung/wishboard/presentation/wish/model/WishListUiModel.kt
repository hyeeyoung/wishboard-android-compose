package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.domain.model.wish.WishItemCount

data class WishListUiModel(
    val shouldShowOnboardingModal: Boolean = false,
    val viewType: WishListViewType = WishListViewType.GRID_2_COLUMN,
    val wishItemCount: WishItemCount? = null,
    val isExcludeOwnedItems: Boolean = false,
)
