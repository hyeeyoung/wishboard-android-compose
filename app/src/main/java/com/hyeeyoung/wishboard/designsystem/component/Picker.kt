package com.hyeeyoung.wishboard.designsystem.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.pxToDp
import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import timber.log.Timber

private const val EMPTY = " "
private const val VISIBLE_ITEM_COUNT = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Picker(
    modifier: Modifier = Modifier,
    itemList: List<String>,
    selectedItem: MutableState<String>,
    visibleItemsCount: Int = VISIBLE_ITEM_COUNT,
    startIdx: Int = 0,
    textModifier: Modifier = Modifier,
    mainTextStyle: TextStyle = WishBoardTheme.typography.suitB3,
    subTextStyle: TextStyle = WishBoardTheme.typography.suitD2,
    enableInfiniteScroll: Boolean = true,
) {
    if (visibleItemsCount < 2) throw IllegalArgumentException("Picker를 사용하기에 적합하지 않음")
    val items =
        if (!enableInfiniteScroll && itemList.size < VISIBLE_ITEM_COUNT) {
            listOf(EMPTY) + itemList.plus(EMPTY)
        } else {
            itemList
        }

    val visibleItemsHalf = visibleItemsCount / 2
    val listScrollCount = if (enableInfiniteScroll) Integer.MAX_VALUE else items.size
    val listFirstItemIdx = if (enableInfiniteScroll) {
        val listScrollHalf = listScrollCount / 2
        listScrollHalf - listScrollHalf % items.size - visibleItemsHalf + startIdx
    } else {
        startIdx
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listFirstItemIdx)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    var itemHeightPx by remember { mutableStateOf(0) }
    val itemHeightDp = itemHeightPx.pxToDp()

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent,
        )
    }

    fun getItem(index: Int) = items[index % items.size]

    LaunchedEffect(listState) {
        snapshotFlow {
            Timber.e(listState.firstVisibleItemIndex.toString())
            listState.firstVisibleItemIndex
        }.map { index -> getItem(index + visibleItemsHalf) }
            .distinctUntilChanged()
            .collect { item -> selectedItem.value = item }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient),
        ) {
            items(listScrollCount) { idx ->
                val isSelected = getItem(idx) == selectedItem.value

                Text(
                    text = getItem(idx),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = if (isSelected) mainTextStyle else subTextStyle,
                    color = WishBoardTheme.colors.gray700,
                    modifier = textModifier
                        .onSizeChanged { size ->
                            itemHeightPx = size.height
                        }
                        .padding(vertical = 8.dp),
                )
            }
        }
    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Preview(showSystemUi = true)
@Composable
fun PreviewPicker() {
    Picker(itemList = NotiType.values().map { it.str }, startIdx = 2, selectedItem = remember { mutableStateOf("") })
}
