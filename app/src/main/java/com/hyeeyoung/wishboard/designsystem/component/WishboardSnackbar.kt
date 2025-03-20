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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.main.MainActivity
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.WishBoardSnackbarVisuals
import com.hyeeyoung.wishboard.presentation.util.extension.toMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

@Composable
@Stable
fun WishBoardSnackbarMessage(snackbarChannel: Channel<WishBoardSnackbarVisuals>) {
    val snackbarHostState = MainActivity.wishBoardSnackbarHostState
    LaunchedEffect(Unit) {
        snackbarChannel.receiveAsFlow().collectLatest { snackBar ->
            if (snackBar.message.isNotEmpty()) {
                snackbarHostState.currentSnackbarData?.dismiss()

                withTimeoutOrNull(snackBar.duration.toMillis()) {
                    snackbarHostState.showSnackbar(snackBar)
                }
            }
        }
    }
}

/**
 * 메인 액티비티 위 화면에서 사용하는 스낵바
 * 메인 액티비티의 snackbarChannel만 업데이트하면 PlusMainSnackbarMessage를 통해 스낵바 보임.
 * @param snackbarChannel 스낵바 시각 정보
 * */
@Composable
@Stable
fun WishBoardGlobalSnackbarMessage(
    snackbarChannel: Channel<WishBoardSnackbarVisuals>,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        snackbarChannel.receiveAsFlow().collectLatest { snackBar ->
            (context as? MainActivity)?.sendSnackbarVisualChannel(snackBar)
        }
    }
}

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

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

@Preview(showSystemUi = true)
@Composable
fun WishBoardSnackbarPreview() {
    WishBoardSnackbar("네트워크 연결 상태를 확인해 주세요.")
}
