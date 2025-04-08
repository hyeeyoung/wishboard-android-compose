package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

data class WishListUiModel(
    val shouldShowOnboardingModal: Boolean = false,
    val withItems: List<WishItem> = emptyList(),
    val fetchState: WishBoardState<Unit> = WishBoardState.Idle,
    val isRefreshing: Boolean = false,
)
