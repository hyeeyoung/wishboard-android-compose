package com.hyeeyoung.wishboard.designsystem.component.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

// 위시리스트, 폴더 상세 등 아이템 선택 모드에서 기본 바텀 네비게이션 바 대신 노출되는 액션 바
@Composable
fun SelectionModeBottomBar(
    selectedItemCount: Int,
    onClickSelectAll: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val isEnabled = selectedItemCount > 0
    val actionTextColor = if (isEnabled) WishBoardTheme.colors.gray700 else WishBoardTheme.colors.gray300

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            WishBoardDivider()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WishBoardTheme.colors.white)
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .noRippleClickable(enabled = isEnabled) { onClickSelectAll() },
                    text = "전체 선택",
                    style = WishBoardTheme.typography.suitB2,
                    color = actionTextColor,
                )

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = if (isEnabled) "${selectedItemCount}개 아이템 선택됨" else "아이템을 선택하세요",
                    style = WishBoardTheme.typography.suitD2,
                    color = WishBoardTheme.colors.gray200,
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .noRippleClickable(enabled = isEnabled) { onClickDelete() },
                    text = "삭제",
                    style = WishBoardTheme.typography.suitB2,
                    color = actionTextColor,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSelectionModeBottomBarDisabled() {
    SelectionModeBottomBar(
        selectedItemCount = 0,
        onClickSelectAll = {},
        onClickDelete = {},
    )
}

@Preview
@Composable
fun PreviewSelectionModeBottomBarEnabled() {
    SelectionModeBottomBar(
        selectedItemCount = 5,
        onClickSelectAll = {},
        onClickDelete = {},
    )
}
