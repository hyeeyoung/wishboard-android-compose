package com.hyeeyoung.wishboard.presentation.sign.model

sealed class WishBoardTextFieldComponent {
    data class Timer(val time: String) : WishBoardTextFieldComponent()
    data object DeleteButton : WishBoardTextFieldComponent()
}
