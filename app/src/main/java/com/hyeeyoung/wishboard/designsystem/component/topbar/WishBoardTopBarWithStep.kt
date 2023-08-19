package com.hyeeyoung.wishboard.designsystem.component.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel

@Composable
fun WishBoardTopBarWithStep(topBarModel: WishBoardTopBarModel, step: Pair<Int, Int>) {
    WishBoardTopBar(
        topBarModel = topBarModel,
        endComponent = { modifier ->
            Row(modifier = modifier) {
                Text(
                    text = stringResource(id = R.string.step, formatArgs = arrayOf(step.first, step.second)),
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        },
    )
}

@Preview
@Composable
fun PreviewWishBoardTopBarWithStep() {
    WishBoardTopBarWithStep(
        topBarModel = WishBoardTopBarModel(title = stringResource(id = R.string.sign_in_email_title)),
        step = Pair(1, 2),
    )
}
