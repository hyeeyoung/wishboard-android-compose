package com.hyeeyoung.wishboard.designsystem.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardWideButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
    text: String,
    shape: Shape = RoundedCornerShape(24.dp),
    isGreen: Boolean = true,
) {
    val color = if (isGreen) {
        ButtonDefaults.buttonColors(
            containerColor = WishBoardTheme.colors.green500,
            disabledContainerColor = WishBoardTheme.colors.gray100,
            contentColor = WishBoardTheme.colors.gray700,
            disabledContentColor = WishBoardTheme.colors.gray300,
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = WishBoardTheme.colors.gray700,
            disabledContainerColor = WishBoardTheme.colors.gray100,
            contentColor = WishBoardTheme.colors.white,
            disabledContentColor = WishBoardTheme.colors.gray300,
        )
    }

    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onClick() },
        shape = shape,
        enabled = enabled,
        colors = color,
        contentPadding = PaddingValues(vertical = 15.dp),
    ) {
        Text(
            text = text,
            style = WishBoardTheme.typography.suitH3,
        )
    }
}

@Composable
@Preview
fun PreviewWishBoardWideButton() {
    WishBoardWideButton(enabled = true, onClick = {}, text = stringResource(id = R.string.sign_in_title))
}
