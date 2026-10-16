package com.hyeeyoung.wishboard.designsystem.component.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.MoreOptionsState
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

// 위시리스트, 폴더 상세 등 아이템 선택 모드에서 기본 바텀 네비게이션 바 대신 노출되는 액션 바
@Composable
fun SelectionModeBottomBar(
    selectedItemCount: Int,
    isAllSelected: Boolean,
    onClickSelectAll: () -> Unit,
    onClickDelete: () -> Unit,
    moreOptions: MoreOptionsState? = null,
) {
    val isEnabled = selectedItemCount > 0
    val actionTextColor = if (isEnabled) WishBoardTheme.colors.gray700 else WishBoardTheme.colors.gray300
    var isMenuExpanded by remember { mutableStateOf(false) }

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
                        // 선택된 아이템이 없어도 "전체 선택"으로 선택을 시작할 수 있어야 하므로 항상 클릭 가능해야 한다.
                        .noRippleClickable { onClickSelectAll() },
                    text = if (isAllSelected) "선택 해제" else "전체 선택",
                    style = WishBoardTheme.typography.suitB2,
                    // 비활성화 상태가 없는 버튼이므로 항상 활성화 컬러를 사용한다.
                    color = WishBoardTheme.colors.gray700,
                )

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = if (isEnabled) "${selectedItemCount}개 아이템 선택됨" else "아이템을 선택하세요",
                    style = WishBoardTheme.typography.suitD2,
                    color = WishBoardTheme.colors.gray200,
                )

                if (moreOptions != null) {
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        Text(
                            modifier = Modifier.noRippleClickable(enabled = isEnabled) { isMenuExpanded = true },
                            text = stringResource(R.string.more_options),
                            style = WishBoardTheme.typography.suitB2,
                            color = actionTextColor,
                        )
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false },
                            offset = DpOffset(x = 0.dp, y = 4.dp),
                        ) {
                            if (moreOptions.showConvertToWish) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.context_menu_convert_to_wish),
                                            style = WishBoardTheme.typography.suitB3,
                                            color = WishBoardTheme.colors.gray700,
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        moreOptions.onClickConvertToWish()
                                    },
                                )
                            }
                            if (moreOptions.showConvertToOwned) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.context_menu_convert_to_owned),
                                            style = WishBoardTheme.typography.suitB3,
                                            color = WishBoardTheme.colors.gray700,
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        moreOptions.onClickConvertToOwned()
                                    },
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.delete),
                                        style = WishBoardTheme.typography.suitB3,
                                        color = WishBoardTheme.colors.pink700,
                                    )
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    onClickDelete()
                                },
                            )
                        }
                    }
                } else {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .noRippleClickable(enabled = isEnabled) { onClickDelete() },
                        text = stringResource(R.string.delete),
                        style = WishBoardTheme.typography.suitB2,
                        color = actionTextColor,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSelectionModeBottomBarDisabled() {
    SelectionModeBottomBar(
        selectedItemCount = 0,
        isAllSelected = false,
        onClickSelectAll = {},
        onClickDelete = {},
    )
}

@Preview
@Composable
fun PreviewSelectionModeBottomBarEnabled() {
    SelectionModeBottomBar(
        selectedItemCount = 5,
        isAllSelected = false,
        onClickSelectAll = {},
        onClickDelete = {},
    )
}

@Preview
@Composable
fun PreviewSelectionModeBottomBarAllSelected() {
    SelectionModeBottomBar(
        selectedItemCount = 117,
        isAllSelected = true,
        onClickSelectAll = {},
        onClickDelete = {},
    )
}
