package com.hyeeyoung.wishboard.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.style.White
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun WishBoardToggleButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onUpdate: (Boolean) -> Unit,
) {
    val trackColor = if (selected) WishBoardTheme.colors.green500 else WishBoardTheme.colors.gray300
    Box(
        modifier = modifier
            .size(width = 40.dp, height = 20.dp)
            .noRippleClickable {
                onUpdate(!selected)
            }
            .background(color = trackColor, shape = RoundedCornerShape(20.dp))
            .padding(2.dp),
        contentAlignment = if (selected) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            onDraw = { drawCircle(color = White) },
        )
    }
}
