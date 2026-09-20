package com.hyeeyoung.wishboard.data.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class WebViewTokenDto(
    val token: String,
    val expiresIn: Int,
)
