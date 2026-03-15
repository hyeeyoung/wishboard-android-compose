package com.hyeeyoung.wishboard.data.remote.model.user

import kotlinx.serialization.Serializable

@Serializable
data class NicknameRequestDto(
    val nickname: String?,
)
