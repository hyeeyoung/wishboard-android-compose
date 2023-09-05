package com.hyeeyoung.wishboard.presentation.model

import androidx.annotation.DrawableRes
import com.hyeeyoung.wishboard.R

data class WishBoardTopBarModel( // TODO 네이밍 변경
    val startIcon: TopBarIcon = TopBarIcon.BACK,
    val title: String? = null,
    val onClickStartIcon: () -> Unit = {},
) {
    enum class TopBarIcon(@DrawableRes val iconRes: Int, val contentDescription: String? = null) {
        BACK(R.drawable.ic_back), CLOSE(R.drawable.ic_close)
    }
}
