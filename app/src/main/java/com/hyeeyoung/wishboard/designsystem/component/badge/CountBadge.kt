package com.hyeeyoung.wishboard.designsystem.component.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme

private const val MAX_DISPLAY_COUNT = 99

/** 아이콘 우상단에 얹는 안 읽은 개수 배지. 0 이하면 아무것도 그리지 않는다. */
@Composable
fun CountBadge(count: Int, modifier: Modifier = Modifier) {
    if (count <= 0) return

    val text = if (count > MAX_DISPLAY_COUNT) "$MAX_DISPLAY_COUNT+" else count.toString()

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
            .background(color = WishBoardTheme.colors.pink700, shape = CircleShape)
            .padding(horizontal = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = WishBoardTheme.typography.suitD3.copy(
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = WishBoardTheme.colors.white,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCountBadge() {
    WishboardTheme {
        Box(modifier = Modifier.padding(8.dp)) {
            CountBadge(count = 10)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCountBadgeOverflow() {
    WishboardTheme {
        Box(modifier = Modifier.padding(8.dp)) {
            CountBadge(count = 120)
        }
    }
}
