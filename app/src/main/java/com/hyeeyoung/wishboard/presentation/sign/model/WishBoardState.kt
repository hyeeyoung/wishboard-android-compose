package com.hyeeyoung.wishboard.presentation.sign.model

import androidx.compose.runtime.Stable

@Stable
sealed class WishBoardState<out T> {
    data object Idle : WishBoardState<Nothing>()
    data object Loading : WishBoardState<Nothing>()
    data object Empty : WishBoardState<Nothing>()
    data class Success<T>(var data: T) : WishBoardState<T>()
    data object Failure : WishBoardState<Nothing>()
}
