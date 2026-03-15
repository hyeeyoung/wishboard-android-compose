package com.hyeeyoung.wishboard.presentation.sign.model.auth

import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

data class SignUiModel(
    val signProcessStatus: WishBoardState<Unit> = WishBoardState.Idle,
    val requestEmailStatus: WishBoardState<Unit> = WishBoardState.Idle,
    val checkVerificationCodeStatus: WishBoardState<Unit> = WishBoardState.Idle,
    val email: String = "",
    val password: String = "",
    val authCode: String = "",
    /** 회원가입 > 이메일 > 기가입자 이메일인 케이스에 사용 */
    val registeredEmail: String? = null,
    /** 회원가입 > 비밀번호 > 기가입자가 가입 시도 시 사용 */
    val isAlreadyRegisteredError: Boolean = false,
    val nonRegisteredEmail: String? = null,
    val isValidEmail: Boolean? = null,
    val isValidPassword: Boolean? = null,
    val isCorrectAuthCode: Boolean? = null,
    val timer: String = "",
)
