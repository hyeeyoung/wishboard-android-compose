package com.hyeeyoung.wishboard.designsystem.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardMiniButton(modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit, text: String) {
    // TODO 전체 버튼 컴포넌트 리팩토링 필요
    Button(
        modifier = modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = WishBoardTheme.colors.gray100,
            contentColor = WishBoardTheme.colors.gray600,
        ),
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 10.dp),
    ) {
        Text(
            text = text,
            style = WishBoardTheme.typography.suitB3,
        )
    }
}
