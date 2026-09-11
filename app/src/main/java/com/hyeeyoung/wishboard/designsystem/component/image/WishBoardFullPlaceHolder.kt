package com.hyeeyoung.wishboard.designsystem.component.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardFullPlaceHolder(modifier: Modifier) {
    Box(
        modifier = modifier,
    ) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            Spacer(modifier = Modifier.weight(0.15f))
            Icon(
                modifier = Modifier.weight(0.7f),
                painter = painterResource(id = R.drawable.ic_app_text_logo),
                tint = WishBoardTheme.colors.gray200,
                contentDescription = "이미지 없음",
            )
            Spacer(modifier = Modifier.weight(0.15f))
        }
    }
}

@Composable
fun WishBoardInitialPlaceHolder(modifier: Modifier, tintColor: Color = WishBoardTheme.colors.gray100) {
    Box(
        modifier = modifier,
    ) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            Icon(
                modifier = Modifier.fillMaxHeight(1f),
                painter = painterResource(id = R.drawable.ic_initial_placeholder),
                tint = tintColor,
                contentDescription = "이미지 없음",
            )
        }
    }
}
