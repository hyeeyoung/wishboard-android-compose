package com.hyeeyoung.wishboard.designsystem.model

import androidx.compose.ui.graphics.Color
import com.hyeeyoung.wishboard.designsystem.style.Gray100
import com.hyeeyoung.wishboard.designsystem.style.Gray300
import com.hyeeyoung.wishboard.designsystem.style.Gray50
import com.hyeeyoung.wishboard.designsystem.style.Gray700
import com.hyeeyoung.wishboard.designsystem.style.Green500
import com.hyeeyoung.wishboard.designsystem.style.White

enum class WishBoardButtonColors(val backgroundColor: Color, val textColor: Color) {
    GREEN(
        backgroundColor = Green500,
        textColor = Gray700,
    ),
    LIGHT_GRAY(
        backgroundColor = Gray50,
        textColor = Gray700,
    ),
    GRAY(
        backgroundColor = Gray100,
        textColor = Gray300,
    ),
    BLACK(
        backgroundColor = Gray700,
        textColor = White,
    ),
}
