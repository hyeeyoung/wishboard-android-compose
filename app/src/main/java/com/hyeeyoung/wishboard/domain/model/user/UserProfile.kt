package com.hyeeyoung.wishboard.domain.model.user

import com.hyeeyoung.wishboard.presentation.common.model.ImageType

data class UserProfile(
    val nickName: String?,
    val profileImage: ImageType.Picture?,
)
