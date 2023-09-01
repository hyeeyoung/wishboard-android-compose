package com.hyeeyoung.wishboard.designsystem.component.divider

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardDivider() = Divider( // TODO 이동
    modifier = Modifier.fillMaxWidth(),
    thickness = 1.dp,
    color = WishBoardTheme.colors.gray100,
)
