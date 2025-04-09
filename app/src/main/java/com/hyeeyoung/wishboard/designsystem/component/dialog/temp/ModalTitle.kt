package com.hyeeyoung.wishboard.designsystem.component.dialog.temp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun ModalTitle(title: String, onDismissRequest: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.TopCenter),
            text = title,
            style = WishBoardTheme.typography.suitH3,
            color = WishBoardTheme.colors.gray700,
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 5.dp, end = 8.dp),
        ) {
            WishBoardIconButton(
                iconRes = R.drawable.ic_close,
                onClick = onDismissRequest,
            )
        }
    }
}
