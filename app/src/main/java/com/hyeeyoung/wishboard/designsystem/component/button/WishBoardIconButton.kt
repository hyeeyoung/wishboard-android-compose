package com.hyeeyoung.wishboard.designsystem.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun LegacyWishBoardIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    Icon(
        modifier = modifier
            .noRippleClickable { onClick() }
            .padding(8.dp),
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        tint = Color.Unspecified,
    )
}

@Composable
fun WishBoardIconButton(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp, // TODO dimen 리소스 추가
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .noRippleClickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = Color.Unspecified,
        )
    }
}
