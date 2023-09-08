package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick()
    }
}

fun Modifier.coloredForeground(alphaColor: Color) = this.drawWithContent {
    drawContent()
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = alphaColor
        }
        canvas.drawRect(
            Rect(
                offset = Offset.Zero,
                size = Size(size.width, size.height),
            ),
            paint,
        )
    }
}
