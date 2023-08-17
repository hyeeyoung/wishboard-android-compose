package com.hyeeyoung.wishboard.designsystem.component.topbar

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardBasicTopBar(onClick: () -> Unit, @StringRes titleRes: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(color = WishBoardTheme.colors.white),
    ) {
        WishBoardIconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            iconRes = R.drawable.ic_back,
            onClick = { onClick() },
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = titleRes),
            style = WishBoardTheme.typography.suitH3,
            color = WishBoardTheme.colors.gray700,
        )
    }
}

@Preview
@Composable
fun PreviewWishBoardBasicTopBar() {
    WishBoardBasicTopBar(onClick = { /*TODO*/ }, titleRes = R.string.sign_in_title)
}
