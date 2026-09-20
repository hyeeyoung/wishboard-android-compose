package com.hyeeyoung.wishboard.domain.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class Token(
    val accessToken: String,
    val refreshToken: String,
)
