package com.hyeeyoung.wishboard.data.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerificationMailRequestDto(
    val email: String,
)
