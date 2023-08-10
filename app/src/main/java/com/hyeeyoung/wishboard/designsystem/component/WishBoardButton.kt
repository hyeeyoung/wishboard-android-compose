package com.hyeeyoung.wishboard.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.util.extension.noRippleClickable

@Composable
fun CartButton(isInCart: Boolean, changeCartState: (Boolean) -> Unit) {
    val btnColor = if (isInCart) WishBoardTheme.colors.green500 else WishBoardTheme.colors.white
    Box(
        modifier = Modifier.noRippleClickable { changeCartState(isInCart) },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(38.dp), onDraw = { drawCircle(color = btnColor) })
        Text(
            text = stringResource(id = R.string.cart_btn_text),
            style = WishBoardTheme.typography.suitD3,
            color = WishBoardTheme.colors.gray700,
        )
    }
}

@Composable
fun WishBoardIconButton(@DrawableRes iconRes: Int, onClick: () -> Unit, contentDescription: String? = null) {
    Icon(
        modifier = Modifier
            .noRippleClickable { onClick() }
            .padding(8.dp),
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
    )
}

@Composable
@Preview
fun PreviewCartButton() {
    CartButton(false, {})
}
