package com.hyeeyoung.wishboard.data.remote.model.auth

import com.hyeeyoung.wishboard.domain.model.auth.AuthInfo
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    val email: String,
    val fcmToken: String,
    val password: String
) {
    companion object {
        fun fromDomain(domain: AuthInfo): AuthRequestDto = AuthRequestDto(
            email = domain.email,
            password = domain.password ?: "",
            fcmToken = domain.fcmToken
        )
    }
}
