package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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

private val AUTO_SCROLL_EDGE_SIZE = 72.dp
private val AUTO_SCROLL_MAX_SPEED = 16.dp
private const val AUTO_SCROLL_FRAME_DELAY_MS = 16L

// 아이템 선택 모드에서 롱프레스 후 드래그로 지나친 아이템을 이어서 선택/해제할 수 있게 한다.
// 스크롤 제스처와 겹치지 않도록 짧은 스와이프는 그대로 스크롤로 동작하고, 롱프레스가 인식된 이후부터만 드래그 선택이 시작된다.
//
// 손가락이 실제로 지나간 좌표의 아이템만 토글하면, 그리드에서 대각선으로 드래그할 때 화면상 지나가지 않은
// 칸(예: 다음 행의 앞쪽 열)은 선택되지 않는 문제가 있다. Google 포토류의 자연스러운 멀티셀렉트처럼 동작하도록,
// 좌표가 아니라 드래그 시작 지점(anchor)과 현재 지점 사이의 "리스트 인덱스 범위"를 계산해서 그 범위 전체를 선택한다.
//
// 손가락이 화면 위/아래 가장자리 근처에 머무르면(iOS 사진 앱처럼) 자동으로 스크롤되면서 선택도 계속 이어지도록,
// 드래그 제스처 감지와 별개로 매 프레임 가장자리 여부를 확인해 스크롤하는 코루틴을 함께 돌린다.
private suspend fun PointerInputScope.detectRangeDragSelect(
    scrollableState: ScrollableState,
    hitIndex: (Offset) -> Int?,
    idAt: (index: Int) -> Long?,
    isSelected: (Long) -> Boolean,
    onSelectedChange: (id: Long, isSelected: Boolean) -> Unit,
) {
    var anchorIndex = -1
    var targetSelected = true
    var previousRange = IntRange.EMPTY
    var lastPosition = Offset.Zero
    // 이번 제스처에서 처음 건드리는 아이템의 원래 선택 상태를 기억해뒀다가, 드래그가 뒤로 물러나
    // 범위에서 빠질 때 무조건 해제하는 대신 제스처 시작 전 상태로 되돌리기 위해 사용한다.
    val originalSelection = mutableMapOf<Long, Boolean>()

    fun setSelected(id: Long, selected: Boolean) {
        originalSelection.getOrPut(id) { isSelected(id) }
        onSelectedChange(id, selected)
    }

    fun applyRange(newRange: IntRange) {
        for (index in previousRange) {
            if (index !in newRange) {
                idAt(index)?.let { id -> onSelectedChange(id, originalSelection[id] ?: false) }
            }
        }
        for (index in newRange) {
            idAt(index)?.let { id -> setSelected(id, targetSelected) }
        }
        previousRange = newRange
    }

    fun updateRangeAt(position: Offset) {
        hitIndex(position)?.let { index ->
            val newRange = if (anchorIndex <= index) anchorIndex..index else index..anchorIndex
            applyRange(newRange)
        }
    }

    coroutineScope {
        launch {
            val edgeSizePx = AUTO_SCROLL_EDGE_SIZE.toPx()
            val maxSpeedPx = AUTO_SCROLL_MAX_SPEED.toPx()

            while (isActive) {
                if (anchorIndex >= 0) {
                    val distanceFromTop = lastPosition.y
                    val distanceFromBottom = size.height - lastPosition.y
                    val scrollAmount = when {
                        distanceFromTop < edgeSizePx ->
                            -maxSpeedPx * (1f - (distanceFromTop / edgeSizePx).coerceIn(0f, 1f))

                        distanceFromBottom < edgeSizePx ->
                            maxSpeedPx * (1f - (distanceFromBottom / edgeSizePx).coerceIn(0f, 1f))

                        else -> 0f
                    }

                    if (scrollAmount != 0f) {
                        scrollableState.scrollBy(scrollAmount)
                        updateRangeAt(lastPosition)
                    }
                }
                delay(AUTO_SCROLL_FRAME_DELAY_MS)
            }
        }

        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                previousRange = IntRange.EMPTY
                originalSelection.clear()
                lastPosition = offset

                val index = hitIndex(offset)
                if (index != null) {
                    anchorIndex = index
                    targetSelected = idAt(index)?.let { !isSelected(it) } ?: true
                    applyRange(index..index)
                } else {
                    anchorIndex = -1
                }
            },
            onDrag = { change, _ ->
                lastPosition = change.position
                if (anchorIndex >= 0) {
                    updateRangeAt(change.position)
                }
            },
            onDragEnd = { anchorIndex = -1 },
            onDragCancel = { anchorIndex = -1 },
        )
    }
}

fun Modifier.dragToSelectItems(
    gridState: LazyGridState,
    enabled: Boolean,
    idAt: (index: Int) -> Long?,
    isSelected: (Long) -> Boolean,
    onSelectedChange: (id: Long, isSelected: Boolean) -> Unit,
): Modifier {
    if (!enabled) return this

    return pointerInput(gridState) {
        detectRangeDragSelect(
            scrollableState = gridState,
            hitIndex = { position ->
                gridState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                    position.x >= item.offset.x && position.x < item.offset.x + item.size.width &&
                        position.y >= item.offset.y && position.y < item.offset.y + item.size.height
                }?.index
            },
            idAt = idAt,
            isSelected = isSelected,
            onSelectedChange = onSelectedChange,
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
        detectRangeDragSelect(
            scrollableState = listState,
            hitIndex = { position ->
                listState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                    position.y >= item.offset && position.y < item.offset + item.size
                }?.index
            },
            idAt = idAt,
            isSelected = isSelected,
            onSelectedChange = onSelectedChange,
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
