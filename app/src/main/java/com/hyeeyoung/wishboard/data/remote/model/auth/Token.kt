package com.hyeeyoung.wishboard.data.remote.model.auth

data class Token(
    val accessToken: String,
    val refreshToken: String,
)
