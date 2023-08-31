package com.hyeeyoung.wishboard.designsystem.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel

@Composable
fun WishBoardTopBar(topBarModel: WishBoardTopBarModel, endComponent: (@Composable (Modifier) -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(color = WishBoardTheme.colors.white),
    ) {
        // 시작 아이콘 영역
        Row(modifier = Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
            topBarModel.startIcons.forEachIndexed { idx, startIcon ->
                if (idx == 0) {
                    Spacer(modifier = Modifier.size(5.dp))
                }

                val onClick = when (startIcon) {
                    WishBoardTopBarModel.TopBarIcon.BACK -> {
                        { topBarModel.onClickStartIcon() }
                    }

                    else -> {
                        { /** TODO */ }
                    }
                }

                WishBoardIconButton(
                    iconRes = startIcon.iconRes,
                    onClick = onClick,
                    contentDescription = startIcon.contentDescription,
                )
            }
        }

        // 타이틀 영역
        if (!topBarModel.title.isNullOrEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = topBarModel.title,
                style = WishBoardTheme.typography.suitH3,
                color = WishBoardTheme.colors.gray700,
            )
        }

        // 끝자락 컴포넌트 영역
        endComponent?.let { component ->
            component(Modifier.align(Alignment.CenterEnd))
        }
    }
}

@Preview
@Composable
fun PreviewWishBoardTopBar() {
    WishBoardTopBar(topBarModel = WishBoardTopBarModel(title = stringResource(id = R.string.sign_in_title)))
}

@Preview
@Composable
fun PreviewWishBoardTopBarWithEndIcons() {
    WishBoardTopBar(
        topBarModel = WishBoardTopBarModel(),
        endComponent = { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WishBoardIconButton(iconRes = R.drawable.ic_trash, onClick = { /*TODO*/ })
                WishBoardIconButton(iconRes = R.drawable.ic_edit, onClick = { /*TODO*/ })
                Spacer(modifier = Modifier.size(8.dp))
            }
        },
    )
}
