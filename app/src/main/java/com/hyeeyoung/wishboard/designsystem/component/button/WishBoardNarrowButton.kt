package com.hyeeyoung.wishboard.designsystem.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardNarrowButton(modifier: Modifier = Modifier, enabled: Boolean, onClick: () -> Unit, text: String) {
    Button(
        modifier = modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(15.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = WishBoardTheme.colors.green500,
            disabledContainerColor = WishBoardTheme.colors.gray100,
            contentColor = WishBoardTheme.colors.gray700,
            disabledContentColor = WishBoardTheme.colors.gray300,
        ),
        contentPadding = PaddingValues(vertical = 7.dp, horizontal = 16.dp),
    ) {
        Text(
            text = text,
            style = WishBoardTheme.typography.suitB3,
        )
    }
}

@Composable
@Preview
fun PreviewWishBoardNarrowButton() {
    WishBoardNarrowButton(enabled = false, onClick = {}, text = stringResource(id = R.string.save))
}
