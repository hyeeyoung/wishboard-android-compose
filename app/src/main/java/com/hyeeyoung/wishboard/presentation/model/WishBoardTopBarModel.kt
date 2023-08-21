package com.hyeeyoung.wishboard.presentation.model

import androidx.annotation.DrawableRes
import com.hyeeyoung.wishboard.R

data class WishBoardTopBarModel(
    val startIcons: List<TopBarIcon> = listOf(TopBarIcon.BACK),
    val title: String? = null,
) {
    enum class TopBarIcon(@DrawableRes val iconRes: Int, val contentDescription: String? = null) {
        BACK(R.drawable.ic_back), CLOSE(R.drawable.ic_close)
    }
}
