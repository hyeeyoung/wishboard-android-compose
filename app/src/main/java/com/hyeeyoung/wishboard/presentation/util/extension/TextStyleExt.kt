package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun TextStyle.toImmutableTextStyle() = this.copy(
    fontSize = this.fontSize.value.dp.toImmutableDp(),
    lineHeight = this.lineHeight.value.dp.toImmutableDp(),
)
