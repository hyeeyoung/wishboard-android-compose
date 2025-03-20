package com.hyeeyoung.wishboard.presentation.sign.model

sealed class WishBoardTextFieldComponent {
    data class Timer(val time: String) : WishBoardTextFieldComponent()
    object DeleteButton : WishBoardTextFieldComponent()
}
