package com.hyeeyoung.wishboard.presentation.folder.model

import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

data class FolderOrderUiModel(
    val originFolders: List<FolderItem> = emptyList(),
    val customFolders: List<FolderItem> = emptyList(),
    val fetchState: WishBoardState<Unit> = WishBoardState.Idle,
    val saveState: WishBoardState<Unit> = WishBoardState.Idle,
    val enabledSaveButton: Boolean = false,
)
