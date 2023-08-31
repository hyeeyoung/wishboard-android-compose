package com.hyeeyoung.wishboard.designsystem.component.topbar

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun WishBoardMainTopBar(@StringRes titleRes: Int, endComponent: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = WishBoardTheme.colors.white)
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = titleRes),
            style = WishBoardTheme.typography.suitH1,
            color = WishBoardTheme.colors.gray700,
        )

        // 끝자락 컴포넌트 영역
        endComponent?.let { component ->
            component()
        }
    }
}

@Preview
@Composable
fun PreviewWishBoardMainTopBar() {
    WishBoardMainTopBar(
        titleRes = R.string.folder,
        endComponent = {
            WishBoardIconButton(modifier = Modifier.padding(end = 8.dp), iconRes = R.drawable.ic_plus, onClick = {
                /*TODO*/
            })
        },
    )
}
