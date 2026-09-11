package com.hyeeyoung.wishboard.designsystem.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalTypography = staticCompositionLocalOf { WishBoardTypography() }
internal val LocalColors = staticCompositionLocalOf { wishBoardLightColors() }

@Composable
fun WishboardTheme(
    darkTheme: Boolean = false,
    typography: WishBoardTypography = WishBoardTheme.typography,
    colors: WishBoardColors = WishBoardTheme.colors,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides typography,
    ) {
        MaterialTheme(
            content = content,
        )
    }
}

// Use with eg. WishBoardTheme.typography.suitH1, WishBoardTheme.colors.green200
object WishBoardTheme {
    val typography: WishBoardTypography
        @Composable get() = LocalTypography.current
    val colors: WishBoardColors
        @Composable get() = LocalColors.current
}
