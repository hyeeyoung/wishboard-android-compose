package com.hyeeyoung.wishboard.presentation.my.model

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

data class MyUiModel(
    val userInfo: UserInfo = UserInfo(),
    val nicknameInput: TextFieldValue = TextFieldValue(),
    val imageUriInput: Uri? = null,
    val passwordInput: String = "",
    val rePasswordInput: String = "",
    val isValidPassword: Boolean? = null,
    val existingNickname: String? = null,
    val accessToken: String = "",
    val isRefreshing: Boolean = false,
    val fetchProfileState: WishBoardState<Unit> = WishBoardState.Idle,
    val updateProfileState: WishBoardState<Unit> = WishBoardState.Idle,
    val updatePasswordState: WishBoardState<Unit> = WishBoardState.Idle,
)
