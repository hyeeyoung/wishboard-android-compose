package com.hyeeyoung.wishboard.presentation.folder.model

import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

data class FolderTabUiModel(
    val folders: List<FolderItem> = emptyList(),
    val fetchState: WishBoardState<Unit> = WishBoardState.Idle,
    val isRefreshing: Boolean = false,
)
