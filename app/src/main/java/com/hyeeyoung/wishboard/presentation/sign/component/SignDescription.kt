package com.hyeeyoung.wishboard.presentation.sign.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun SignDescription(@StringRes titleRes: Int, @StringRes descriptionRes: Int) {
    Column(
        modifier = Modifier.padding(top = 24.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(id = titleRes),
            color = WishBoardTheme.colors.gray700,
            style = WishBoardTheme.typography.suitH0,
        )

        Text(
            text = stringResource(id = descriptionRes),
            color = WishBoardTheme.colors.gray300,
            style = WishBoardTheme.typography.suitD2M,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewSignDescription() {
    SignDescription(titleRes = R.string.sign_in_email_title, descriptionRes = R.string.sign_in_email_description)
}
