package com.hyeeyoung.wishboard.designsystem.component.text

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardEmptyText(modifier: Modifier = Modifier, @StringRes guideTextRes: Int) =
    Text(
        modifier = modifier,
        text = stringResource(id = guideTextRes),
        style = WishBoardTheme.typography.suitD2M,
        color = WishBoardTheme.colors.gray200,
    )
