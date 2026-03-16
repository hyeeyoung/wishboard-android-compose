package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.folder.model.FolderOrderUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack

@Composable
fun FolderOrderScreen(
    navController: NavController,
    viewModel: FolderOrderViewModel = hiltViewModel(),
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    WishBoardGlobalSnackbarMessage(
        snackbarChannel = viewModel.snackBarChannel,
    )

    FolderOrderContent(
        uiModel = uiModel,
        onClickSave = {
            viewModel.saveReorderedFolder(
                afterSuccess = {
                    navController.safePopBackStack()
                },
            )
        },
        onClickClose = navController::safePopBackStack,
    )
}

@Composable
fun FolderOrderContent(uiModel: FolderOrderUiModel, onClickClose: () -> Unit, onClickSave: () -> Unit) {
    val state = remember { DragDropListState(uiModel.customFolders) }

    // 외부 items가 바뀌면 동기화
    LaunchedEffect(uiModel.customFolders) { state.items = uiModel.customFolders }

    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                title = "폴더 정렬",
                startIcon = WishBoardTopBarModel.TopBarIcon.CLOSE,
                onClickStartIcon = onClickClose,
            ),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize(),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(contentPadding = PaddingValues(top = 18.dp, bottom = 36.dp)) {
                    itemsIndexed(
                        items = state.items,
                        key = { _, item -> item }, // swap 애니메이션의 핵심
                    ) { index, item ->
                        DraggableItem(
                            state = state,
                            index = index,
                            modifier = Modifier.animateItem(
                                placementSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow, // 너무 빠르면 드래그와 충돌
                                ),
                            ),
                        ) {
                            FolderItem(item)
                        }
                    }
                }
//                LazyColumn(
//
//                ) {
//                    itemsIndexed(uiModel.customFolders) { idx, folder ->
//                        FolderItem(
//                            folder = folder,
//                        )
//                    }
//                }

                Box(
                    modifier = Modifier
                        .zIndex(2f)
                        .fillMaxWidth()
                        .height(36.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x80FFFFFF),
                                    Color.White,
                                ),
                            ),
                        ),
                )
            }

            WishBoardWideButton(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.spacing_base))
                    .padding(bottom = 30.dp),
                enabled = true, // TODO
                onClick = {
                    onClickSave()
                },
                text = stringResource(id = R.string.save),
            )
        }
    }
}

@Composable
private fun FolderItem(folder: FolderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            model = folder.thumbnail,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            text = folder.name,
            style = WishBoardTheme.typography.suitD2,
            color = WishBoardTheme.colors.gray700,
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_drag_handle),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

data class DragDropState(
    val draggingIndex: Int? = null,
    val dragOffsetY: Float = 0f,
)

class DragDropListState(initialItems: List<FolderItem>) {
    var items by mutableStateOf(initialItems)
    var draggingIndex by mutableStateOf<Int?>(null)
    var dragOffsetY by mutableStateOf(0f)
    val itemBounds = mutableStateMapOf<Int, ClosedFloatingPointRange<Float>>()

    fun onDragStart(index: Int) {
        draggingIndex = index
        dragOffsetY = 0f
    }

    fun onDrag(delta: Float) {
        val fromIndex = draggingIndex ?: return
        dragOffsetY += delta

        val fromBounds = itemBounds[fromIndex] ?: return
        val itemHeight = fromBounds.endInclusive - fromBounds.start
        val currentCenter = fromBounds.start + dragOffsetY + itemHeight / 2f

        // 현재 드래그 중심이 어느 아이템 위에 있는지 계산
        val targetIndex = itemBounds.entries
            .minByOrNull { (_, range) ->
                val center = (range.start + range.endInclusive) / 2f
                kotlin.math.abs(currentCenter - center)
            }?.key ?: fromIndex

        if (targetIndex != fromIndex) {
            val targetBounds = itemBounds[targetIndex] ?: return

            // swap 후 드래그 아이템이 튀지 않도록 offset 보정
            val correction = targetBounds.start - fromBounds.start
            dragOffsetY -= correction

            // 실제 리스트 swap
            items = items.toMutableList().also {
                val item = it.removeAt(fromIndex)
                it.add(targetIndex, item)
            }
            draggingIndex = targetIndex
        }
    }

    fun onDragEnd() {
        draggingIndex = null
        dragOffsetY = 0f
    }
}

@Composable
fun DraggableItem(
    state: DragDropListState,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isDragging = state.draggingIndex == index

    Box(
        modifier = modifier
            .zIndex(if (isDragging) 1f else 0f)
            .graphicsLayer {
                translationY = if (isDragging) state.dragOffsetY else 0f
                scaleX = if (isDragging) 1.04f else 1f
                scaleY = if (isDragging) 1.04f else 1f
                shadowElevation = if (isDragging) 16f else 0f
                alpha = if (isDragging) 0.93f else 1f
            }
            .onGloballyPositioned { coords ->
                val top = coords.positionInRoot().y
                state.itemBounds[index] = top..(top + coords.size.height)
            }
            .pointerInput(index) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { state.onDragStart(index) },
                    onDrag = { _, delta -> state.onDrag(delta.y) },
                    onDragEnd = { state.onDragEnd() },
                    onDragCancel = { state.onDragEnd() },
                )
            },
    ) {
        content()
    }
}

@Composable
@Preview
fun PreviewFolderOrderScreen() {
    FolderOrderContent(
        uiModel = FolderOrderUiModel(
            originFolders = emptyList(),
            customFolders = listOf(
                FolderItem(
                    id = 0L,
                    name = "아우터",
                    thumbnail = "https://url.kr/8vwf1e",
                ),
                FolderItem(
                    id = 1L,
                    name = "신발",
                    thumbnail = "https://url.kr/8vwf1e",
                ),
                FolderItem(
                    id = 2L,
                    name = "가방",
                    thumbnail = "https://url.kr/8vwf1e",
                ),
                FolderItem(
                    id = 3L,
                    name = "하의",
                    thumbnail = "https://url.kr/8vwf1e",
                ),
                FolderItem(
                    id = 4L,
                    name = "인테리어",
                    thumbnail = "https://url.kr/8vwf1e",
                ),
            ),
        ),
        onClickClose = {},
        onClickSave = {},
    )
}
