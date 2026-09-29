package com.hyeeyoung.wishboard.presentation.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.chuckerteam.chucker.api.Chucker
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.annotation.DefaultPreview
import kotlin.math.roundToInt

private val BUTTON_SIZE = 56.dp

/**
 * 화면 어디로든 드래그해 이동할 수 있는 디버그 진입용 플로팅 버튼.
 * 탭하면 Chucker(네트워크 인스펙터) 화면으로 진입한다.
 */
@Composable
fun DebugFloatingButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val buttonSizePx = with(density) { BUTTON_SIZE.toPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    var offsetX by remember { mutableFloatStateOf(screenWidthPx - buttonSizePx * 1.5f) }
    var offsetY by remember { mutableFloatStateOf(screenHeightPx * 0.6f) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(BUTTON_SIZE)
            .background(color = WishBoardTheme.colors.gray300.copy(alpha = 0.7f), shape = CircleShape)
            // detectDragGestures는 터치 슬롭을 넘는 이동이 있어야만 onDragStart/onDragEnd가 발생해
            // 단순 탭에서는 아예 호출되지 않으므로, 드래그와 탭을 각각 별도의 pointerInput으로 분리해야 한다.
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidthPx - buttonSizePx)
                    offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeightPx - buttonSizePx)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    context.startActivity(Chucker.getLaunchIntent(context))
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = Icons.Filled.Build,
            tint = WishBoardTheme.colors.gray50,
            contentDescription = "디버그뷰",
        )
    }
}

@DefaultPreview
@Composable
fun PreviewDebugFloatingButton() {
    DebugFloatingButton()
}
