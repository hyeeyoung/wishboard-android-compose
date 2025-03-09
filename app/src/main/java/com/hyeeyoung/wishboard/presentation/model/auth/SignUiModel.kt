package com.hyeeyoung.wishboard.presentation.model.auth

import com.hyeeyoung.wishboard.presentation.model.WishBoardState

data class SignUiModel(
    val signProcessStatus: WishBoardState<Unit> = WishBoardState.Idle,
    val email: String = "",
    val password: String = "",
    val authCode: String = "",
    val registeredEmail: String? = null, // TODO 다시 생각해보기
    val nonRegisteredEmail: String? = null,
    val isValidEmail: Boolean? = null,
    val isValidPassword: Boolean? = null,
    val isCorrectAuthCode: Boolean? = null,
)
