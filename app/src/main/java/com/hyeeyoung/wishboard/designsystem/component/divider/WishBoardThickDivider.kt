package com.hyeeyoung.wishboard.designsystem.component.divider

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardThickDivider() = Divider(
    modifier = Modifier.fillMaxWidth(),
    thickness = 6.dp,
    color = WishBoardTheme.colors.gray50,
)
