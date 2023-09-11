package com.hyeeyoung.wishboard.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
fun WishBoardEmptyView(modifier: Modifier = Modifier, @StringRes guideTextRes: Int, @DrawableRes iconRes: Int? = null) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        iconRes?.let { icon ->
            Icon(painter = painterResource(id = icon), contentDescription = null, tint = Color.Unspecified)
            Spacer(modifier = Modifier.size(20.dp))
        }

        Text(
            text = stringResource(id = guideTextRes),
            style = WishBoardTheme.typography.suitD2M,
            color = WishBoardTheme.colors.gray200,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewWishBoardEmptyView() {
    WishBoardEmptyView(
        modifier = Modifier.fillMaxSize(),
        guideTextRes = R.string.empty_noti_guide_text,
        iconRes = R.drawable.ic_noti_large,
    )
}
