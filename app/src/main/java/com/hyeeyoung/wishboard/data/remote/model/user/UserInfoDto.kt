package com.hyeeyoung.wishboard.data.remote.model.user

import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    var email: String = "",
    var nickname: String? = null,
    @SerialName("profileImgUrl")
    var profileImage: String? = null,
    @SerialName("pushState")
    val isPushAllowed: Boolean? = null,
) {
    fun toDomain(): UserInfo = UserInfo(
        email = email,
        nickname = nickname ?: "",
        profileImage = profileImage,
        isPushAllowed = isPushAllowed ?: false,
    )
}
