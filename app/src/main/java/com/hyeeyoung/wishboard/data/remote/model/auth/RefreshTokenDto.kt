package com.hyeeyoung.wishboard.data.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenDto(
    val refreshToken: String,
)
