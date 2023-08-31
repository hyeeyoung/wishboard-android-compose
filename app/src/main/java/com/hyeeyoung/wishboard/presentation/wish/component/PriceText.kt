package com.hyeeyoung.wishboard.presentation.wish.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.setPriceFormat

@Composable
fun PriceText(modifier: Modifier = Modifier, price: Int, priceStyle: TextStyle, wonStyle: TextStyle) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = price.setPriceFormat(),
            style = priceStyle,
            color = WishBoardTheme.colors.gray700,
        )
        Text(
            text = stringResource(id = R.string.won),
            style = wonStyle,
            color = WishBoardTheme.colors.gray700,
        )
    }
}
