package com.hyeeyoung.wishboard.domain.model.user

import okhttp3.MultipartBody

data class UserProfile(
    val nickName: String?,
    val profileImage: MultipartBody.Part?,
)
