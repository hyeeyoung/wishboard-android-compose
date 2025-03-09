package com.hyeeyoung.wishboard.domain.model.auth

data class AuthInfo(
    val email: String,
    val fcmToken: String,
    /** 패스워드, 이메일 로그인에서는 없어도 됨 */
    val password: String? = ""
)
