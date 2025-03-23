package com.hyeeyoung.wishboard.presentation.my.model

import android.net.Uri
import com.hyeeyoung.wishboard.domain.model.noti.UserInfo

data class MyUiModel(
    val userInfo: UserInfo = UserInfo(),
    val nameInput: String = "",
    val imageUriInput: Uri? = null,
    val passwordInput: String = "",
    val rePasswordInput: String = "",
    val isValidPassword: Boolean? = null,
)
