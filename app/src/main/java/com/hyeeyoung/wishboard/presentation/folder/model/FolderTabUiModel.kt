package com.hyeeyoung.wishboard.presentation.folder.model

import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

data class FolderTabUiModel(
//    val fetchState: WishBoardState<Unit> = WishBoardState.Idle,
    val existingFolderName: String? = null,
    val addState: WishBoardState<Unit> = WishBoardState.Idle,
    val updateState: WishBoardState<Unit> = WishBoardState.Idle,
    val deleteState: WishBoardState<Unit> = WishBoardState.Idle,
)
