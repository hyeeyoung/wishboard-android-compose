package com.hyeeyoung.wishboard.data.remote.model.auth

import com.hyeeyoung.wishboard.domain.model.auth.Token
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    /** 임시 닉네임, 수정 이력이 있는 유저는 null로 내려옴 */
    val tempNickname: String? = null,
    val token: Token
)
