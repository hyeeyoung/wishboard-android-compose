package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ripple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp

inline fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    debounceIntervalMillis: Long = 400L,
    crossinline onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    clickable(
        indication = null,
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceIntervalMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

inline fun Modifier.rippleClickable(
    color: Color = Color(0x42000000),
    radius: Dp = Dp.Unspecified,
    enabled: Boolean = true,
    ripple: Boolean = true,
    bounded: Boolean = true,
    debounceIntervalMillis: Long = 400L,
    crossinline onClick: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val indication = ripple(
        color = color,
        bounded = bounded,
        radius = radius,
    ).takeIf { ripple }

    clickable(
        indication = indication,
        enabled = enabled,
        interactionSource = interactionSource,
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceIntervalMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

// 아이템 선택 모드에서 롱프레스 후 드래그로 지나친 아이템을 이어서 선택/해제할 수 있게 한다.
// 스크롤 제스처와 겹치지 않도록 짧은 스와이프는 그대로 스크롤로 동작하고, 롱프레스가 인식된 이후부터만 드래그 선택이 시작된다.
fun Modifier.dragToSelectItems(
    gridState: LazyGridState,
    enabled: Boolean,
    idAt: (index: Int) -> Long?,
    isSelected: (Long) -> Boolean,
    onSelectedChange: (id: Long, isSelected: Boolean) -> Unit,
): Modifier {
    if (!enabled) return this

    return pointerInput(gridState) {
        var targetSelected = true
        val processedIndices = mutableSetOf<Int>()

        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                processedIndices.clear()
                gridState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item ->
                        offset.x >= item.offset.x && offset.x < item.offset.x + item.size.width &&
                            offset.y >= item.offset.y && offset.y < item.offset.y + item.size.height
                    }
                    ?.let { item ->
                        idAt(item.index)?.let { id ->
                            targetSelected = !isSelected(id)
                            onSelectedChange(id, targetSelected)
                            processedIndices.add(item.index)
                        }
                    }
            },
            onDrag = { change, _ ->
                gridState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item ->
                        change.position.x >= item.offset.x && change.position.x < item.offset.x + item.size.width &&
                            change.position.y >= item.offset.y && change.position.y < item.offset.y + item.size.height
                    }
                    ?.let { item ->
                        if (processedIndices.add(item.index)) {
                            idAt(item.index)?.let { id -> onSelectedChange(id, targetSelected) }
                        }
                    }
            },
        )
    }
}

// LazyColumn(리스트 뷰) 버전 — 세로 한 축만 히트테스트하면 되는 점을 제외하면 그리드 버전과 동일하다.
fun Modifier.dragToSelectItems(
    listState: LazyListState,
    enabled: Boolean,
    idAt: (index: Int) -> Long?,
    isSelected: (Long) -> Boolean,
    onSelectedChange: (id: Long, isSelected: Boolean) -> Unit,
): Modifier {
    if (!enabled) return this

    return pointerInput(listState) {
        var targetSelected = true
        val processedIndices = mutableSetOf<Int>()

        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                processedIndices.clear()
                listState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item -> offset.y >= item.offset && offset.y < item.offset + item.size }
                    ?.let { item ->
                        idAt(item.index)?.let { id ->
                            targetSelected = !isSelected(id)
                            onSelectedChange(id, targetSelected)
                            processedIndices.add(item.index)
                        }
                    }
            },
            onDrag = { change, _ ->
                listState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item ->
                        change.position.y >= item.offset && change.position.y < item.offset + item.size
                    }
                    ?.let { item ->
                        if (processedIndices.add(item.index)) {
                            idAt(item.index)?.let { id -> onSelectedChange(id, targetSelected) }
                        }
                    }
            },
        )
    }
}

fun Modifier.coloredForeground(alphaColor: Color) = this.drawWithContent {
    drawContent()
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = alphaColor
        }
        canvas.drawRect(
            Rect(
                offset = Offset.Zero,
                size = Size(size.width, size.height),
            ),
            paint,
        )
    }
}
