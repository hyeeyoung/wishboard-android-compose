package com.hyeeyoung.wishboard.data.remote.model.user

import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    var email: String = "",
    var nickname: String? = null,
    @SerialName("profile_img_url")
    var profileImage: String? = null,
    @SerialName("push_state")
    val isPushAllowed: Int? = null,
) {
    fun toDomain(): UserInfo = UserInfo(
        email = email,
        nickname = nickname ?: "",
        profileImage = profileImage,
        isPushAllowed = if (isPushAllowed != null) isPushAllowed != 0 else null,
    )
}
