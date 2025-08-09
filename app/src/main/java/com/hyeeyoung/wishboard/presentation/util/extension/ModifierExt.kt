package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp

inline fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    debounceIntervalMillis: Long = 400L,
    crossinline onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    clickable(
        indication = null,
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceIntervalMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

inline fun Modifier.rippleClickable(
    color: Color = Color(0x42000000),
    radius: Dp = Dp.Unspecified,
    enabled: Boolean = true,
    ripple: Boolean = true,
    bounded: Boolean = true,
    debounceIntervalMillis: Long = 400L,
    crossinline onClick: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val indication = ripple(
        color = color,
        bounded = bounded,
        radius = radius,
    ).takeIf { ripple }

    clickable(
        indication = indication,
        enabled = enabled,
        interactionSource = interactionSource,
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceIntervalMillis) {
            lastClickTime = currentTime
            onClick()
        }
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
