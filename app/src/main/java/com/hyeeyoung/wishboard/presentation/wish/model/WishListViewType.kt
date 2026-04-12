package com.hyeeyoung.wishboard.presentation.wish.model

import androidx.annotation.DrawableRes
import com.hyeeyoung.wishboard.R

enum class WishListViewType(@DrawableRes val iconRes: Int, val description: String) {
    GRID_2_COLUMN(R.drawable.ic_grid2, "2열 그리드"),
    GRID_3_COLUMN(
        R.drawable.ic_grid3,
        "3열 그리드",
    ), LIST(
        R.drawable.ic_list,
        "리스트",
    )
    ;

    fun next(): WishListViewType {
        val nextOrdinal = (ordinal + 1) % entries.size
        return entries[nextOrdinal]
    }
}
