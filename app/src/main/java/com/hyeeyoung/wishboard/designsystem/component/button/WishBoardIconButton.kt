package com.hyeeyoung.wishboard.designsystem.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun WishBoardIconButton(
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
