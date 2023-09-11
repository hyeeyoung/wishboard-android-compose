package com.hyeeyoung.wishboard.presentation.model

import androidx.annotation.StringRes

data class WishBoardDialogTextRes(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val dismissBtnTextRes: Int,
    @StringRes val confirmBtnTextRes: Int,
)
