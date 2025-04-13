package com.hyeeyoung.wishboard.data.remote.model.auth

import com.hyeeyoung.wishboard.domain.model.auth.Token
import kotlinx.serialization.Serializable

@Serializable
data class ResponseToken(
    val token: Token,
)
