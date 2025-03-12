package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.model.WishBoardState

data class WishListUiModel(
    val withItems: List<WishItem> = emptyList(),
    val fetchState: WishBoardState<Unit> = WishBoardState.Idle,
    val isRefreshing: Boolean = false,
)
