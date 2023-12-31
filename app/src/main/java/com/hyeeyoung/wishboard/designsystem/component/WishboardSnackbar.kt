package com.hyeeyoung.wishboard.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

@Composable
fun WishBoardSnackbarHost(hostState: SnackbarHostState) =
    SnackbarHost(hostState = hostState) { data ->
        WishBoardSnackbar(message = data.visuals.message)
    }

@Composable
fun WishBoardSnackbar(message: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .background(WishBoardTheme.colors.gray700, RoundedCornerShape(45.dp))
                .padding(vertical = 16.dp, horizontal = 32.dp),
            text = message,
            color = WishBoardTheme.colors.white,
            textAlign = TextAlign.Center,
            style = WishBoardTheme.typography.suitD2M,
        )
        Spacer(modifier = Modifier.size(32.dp))
    }
}

fun SnackbarHostState.showSnackbar(message: String, coroutineScope: CoroutineScope) =
    coroutineScope.launch {
        withTimeout(2000) {
            showSnackbar(message)
        }
    }

@Preview(showSystemUi = true)
@Composable
fun WishBoardSnackbarPreview() {
    WishBoardSnackbar("네트워크 연결 상태를 확인해 주세요.")
}
