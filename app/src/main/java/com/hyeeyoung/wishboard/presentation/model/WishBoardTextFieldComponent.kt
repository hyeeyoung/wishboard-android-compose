package com.hyeeyoung.wishboard.presentation.model

sealed class WishBoardTextFieldComponent {
    data class Timer(val minute: Int, val second: Int) : WishBoardTextFieldComponent()
    object DeleteButton : WishBoardTextFieldComponent()
}
