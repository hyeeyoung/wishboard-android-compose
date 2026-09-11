package com.hyeeyoung.wishboard.designsystem.component.divider

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardDivider() = HorizontalDivider(
    modifier = Modifier.fillMaxWidth(),
    color = WishBoardTheme.colors.gray100,
)
