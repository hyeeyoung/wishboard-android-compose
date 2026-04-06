package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
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
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

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
        onMoveFolder = viewModel::moveFolder,
        resetOrder = viewModel::resetOrder,
    )
}

@Composable
fun FolderOrderContent(
    uiModel: FolderOrderUiModel,
    onClickClose: () -> Unit,
    onClickSave: () -> Unit,
    onMoveFolder: (Int, Int) -> Unit = { _, _ -> },
    resetOrder: () -> Unit,
) {
    val listState = rememberLazyListState()
    // 드래그 중인 아이템의 원래 인덱스 (-1 = 드래그 없음)
    var draggedIndex by remember { mutableIntStateOf(-1) }
    // 드래그 중 시각적으로 이동할 목표 인덱스
    var dragTargetIndex by remember { mutableIntStateOf(-1) }
    // 드래그된 Y 오프셋 (px)
    var dragOffset by remember { mutableFloatStateOf(0f) }
    // 아이템 높이 (px), 첫 번째 측정 시 저장
    var itemHeightPx by remember { mutableFloatStateOf(0f) }

    // 드래그 중 viewport 경계 도달 시 자동 스크롤
    LaunchedEffect(Unit) {
        while (isActive) {
            if (draggedIndex != -1 && itemHeightPx > 0f) {
                val draggedInfo = listState.layoutInfo.visibleItemsInfo
                    .firstOrNull { it.index == draggedIndex }

                if (draggedInfo != null) {
                    val viewport = listState.layoutInfo.viewportSize.height.toFloat()
                    val itemVisualTop = draggedInfo.offset.toFloat() + dragOffset
                    val itemVisualBottom = itemVisualTop + itemHeightPx

                    // 아이템이 viewport를 벗어난 정도에 비례해 스크롤 속도 결정
                    val scrollBy = when {
                        itemVisualTop < 0f ->
                            (itemVisualTop / itemHeightPx * 20f).coerceIn(-20f, -1f)

                        itemVisualBottom > viewport ->
                            ((itemVisualBottom - viewport) / itemHeightPx * 20f).coerceIn(1f, 20f)

                        else -> 0f
                    }

                    if (scrollBy != 0f) {
                        listState.scrollBy(scrollBy)
                        // 스크롤만큼 dragOffset 보정 → 아이템이 화면 위치 유지
                        dragOffset += scrollBy
                        dragTargetIndex = (draggedIndex + (dragOffset / itemHeightPx).roundToInt())
                            .coerceIn(0, listState.layoutInfo.totalItemsCount - 1)
                    }
                }
            }
            delay(16L) // ~60fps
        }
    }

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
                LazyColumn(
                    userScrollEnabled = draggedIndex == -1,
                    contentPadding = PaddingValues(top = 18.dp, bottom = 36.dp),
                ) {
                    itemsIndexed(
                        items = uiModel.customFolders,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        val isDragging = index == draggedIndex

                        // 드래그 중이 아닌 아이템이 비켜줄 방향 계산
                        val targetShift = when {
                            draggedIndex == -1 || isDragging -> 0f
                            draggedIndex < dragTargetIndex &&
                                index in (draggedIndex + 1)..dragTargetIndex -> -itemHeightPx
                            draggedIndex > dragTargetIndex &&
                                index in dragTargetIndex until draggedIndex -> itemHeightPx
                            else -> 0f
                        }
                        val animatedShift by animateFloatAsState(
                            targetValue = targetShift,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "itemShift",
                        )

                        FolderItem(
                            folder = item,
                            isDragging = isDragging,
                            translationY = if (isDragging) dragOffset else animatedShift,
                            modifier = Modifier.onSizeChanged { size ->
                                if (itemHeightPx == 0f) itemHeightPx = size.height.toFloat()
                            },
                            onDragStart = {
                                draggedIndex = index
                                dragTargetIndex = index
                                dragOffset = 0f
                            },
                            onDrag = { delta ->
                                dragOffset += delta
                                if (itemHeightPx > 0f) {
                                    dragTargetIndex = (draggedIndex + (dragOffset / itemHeightPx).roundToInt())
                                        .coerceIn(0, uiModel.customFolders.size - 1)
                                }
                            },
                            onDragEnd = {
                                if (draggedIndex != -1 && dragTargetIndex != draggedIndex) {
                                    onMoveFolder(draggedIndex, dragTargetIndex)
                                }
                                draggedIndex = -1
                                dragTargetIndex = -1
                                dragOffset = 0f
                            },
                        )
                    }
                }

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
                    .padding(bottom = 8.dp),
                enabled = uiModel.enabledSaveButton,
                onClick = {
                    onClickSave()
                },
                text = stringResource(id = R.string.save),
            )

            Row(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .noRippleClickable {
                        resetOrder()
                    }
                    .padding(vertical = 4.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_retry),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )

                Text(
                    modifier = Modifier.padding(start = 3.dp),
                    text = "이전 상태로 되돌리기",
                    style = WishBoardTheme.typography.suitD2,
                    color = WishBoardTheme.colors.gray600,
                )
            }
        }
    }
}

@Composable
private fun FolderItem(
    modifier: Modifier = Modifier,
    folder: FolderItem,
    isDragging: Boolean = false,
    translationY: Float = 0f,
    onDragStart: () -> Unit = {},
    onDrag: (Float) -> Unit = {},
    onDragEnd: () -> Unit = {},
) {
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(if (isDragging) 1f else 0f)
            .graphicsLayer { this.translationY = translationY }
            .shadow(
                elevation = if (isDragging) 6.dp else 0.dp,
                shape = RoundedCornerShape(8.dp),
                clip = false,
            )
            .background(WishBoardTheme.colors.white)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { currentOnDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentOnDrag(dragAmount.y)
                    },
                    onDragEnd = { currentOnDragEnd() },
                    onDragCancel = { currentOnDragEnd() },
                )
            },
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
        resetOrder = {},
    )
}
