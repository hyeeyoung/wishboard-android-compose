package com.hyeeyoung.wishboard.designsystem.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun LegacyWishBoardIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    Icon(
        modifier = modifier
            .noRippleClickable { onClick() }
            .padding(8.dp),
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        tint = Color.Unspecified,
    )
}

@Composable
fun WishBoardIconButton(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp, // TODO dimen 리소스 추가
    tint: Color = Color.Unspecified,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .noRippleClickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

// 위시리스트, 폴더 상세 등 아이템 목록 화면의 탑바에서 공통으로 쓰이는 '아이템 선택 모드 진입' 버튼
@Composable
fun SelectionModeIconButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    LegacyWishBoardIconButton(
        modifier = modifier,
        iconRes = R.drawable.ic_main_top_bar_check,
        onClick = onClick,
        contentDescription = "아이템 선택",
    )
}
