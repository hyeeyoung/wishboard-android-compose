package com.hyeeyoung.wishboard.data.remote.model.auth

import com.hyeeyoung.wishboard.domain.model.auth.AuthInfo
import kotlinx.serialization.Serializable

@Serializable
data class EmailAuthRequestDto(
    val email: String,
    val fcmToken: String,
    val verify: Boolean = true,
) {
    companion object {
        fun fromDomain(domain: AuthInfo): EmailAuthRequestDto = EmailAuthRequestDto(
            email = domain.email,
            fcmToken = domain.fcmToken,
        )
    }
}
