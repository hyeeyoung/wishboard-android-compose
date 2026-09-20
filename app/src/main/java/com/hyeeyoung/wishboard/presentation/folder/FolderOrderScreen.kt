package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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
    onMoveFolder: (from: Int, to: Int) -> Unit = { _, _ -> },
    resetOrder: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onMoveFolder(from.index, to.index)
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
                    state = lazyListState,
                    contentPadding = PaddingValues(top = 18.dp, bottom = 36.dp),
                ) {
                    items(uiModel.customFolders, key = { it.id }) { item ->
                        ReorderableItem(reorderableLazyListState, key = item.id) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 2.dp else 0.dp)
                            Surface(shadowElevation = elevation) {
                                FolderItem(
                                    dragModifier = Modifier.draggableHandle(),
                                    folder = item,
                                )
                            }
                        }
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

            if (uiModel.enabledSaveButton) {
                Row(
                    modifier = Modifier
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

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FolderItem(
    dragModifier: Modifier,
    folder: FolderItem,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WishBoardTheme.colors.white)
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
            modifier = dragModifier,
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
