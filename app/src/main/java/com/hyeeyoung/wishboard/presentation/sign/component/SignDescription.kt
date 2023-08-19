package com.hyeeyoung.wishboard.presentation.sign.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun SignDescription(@StringRes descriptionRes: Int, @DrawableRes iconRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = painterResource(id = iconRes), tint = Color.Unspecified, contentDescription = null)
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = stringResource(id = descriptionRes),
            color = WishBoardTheme.colors.gray700,
            style = WishBoardTheme.typography.suitD2M,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewSignDescription() {
    SignDescription(descriptionRes = R.string.sign_in_email_description, iconRes = R.drawable.ic_email)
}
