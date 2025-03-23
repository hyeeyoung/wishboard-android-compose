package com.hyeeyoung.wishboard.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    var email: String = "",
    var nickname: String = "",
    var profileImage: String? = null,
    val isPushAllowed: Boolean? = null,
)
