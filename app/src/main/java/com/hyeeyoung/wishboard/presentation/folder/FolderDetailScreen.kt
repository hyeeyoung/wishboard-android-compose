package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.bottombar.SelectionModeBottomBar
import com.hyeeyoung.wishboard.designsystem.component.button.SelectionModeIconButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardTwoButtonDialog
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.dragToSelectItems
import com.hyeeyoung.wishboard.presentation.util.extension.rippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.util.getFakePagingData
import com.hyeeyoung.wishboard.presentation.wish.component.WishItemForGridView
import com.hyeeyoung.wishboard.presentation.wish.component.WishItemForListView
import com.hyeeyoung.wishboard.presentation.wish.model.WishListViewType
import com.hyeeyoung.wishboard.presentation.wish.screen.SelectableCircleButton
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun FolderDetailScreen(
    navController: NavController,
    folderName: String,
    folderId: Long,
    viewModel: FolderDetailViewModel = hiltViewModel(),
) {
    val wishList = viewModel.wishList.collectAsLazyPagingItems()
    val totalItemCount by viewModel.totalItemCount.collectAsStateWithLifecycle()
    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()
    val isAllSelected by viewModel.isAllSelected.collectAsStateWithLifecycle()
    val selectedItemIds by viewModel.selectedItemIds.collectAsStateWithLifecycle()
    val excludedItemIds by viewModel.excludedItemIds.collectAsStateWithLifecycle()
    val deleteSelectedItemsState by viewModel.deleteSelectedItemsState.collectAsStateWithLifecycle()
    val viewType by viewModel.viewType.collectAsStateWithLifecycle()
    val isExcludeOwnedItems by viewModel.isExcludeOwnedItems.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()
    val lazyListState = rememberLazyListState()
    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    if (viewType != WishListViewType.LIST) {
        MainScreen.FolderDetail.ScrollToTopEffect(lazyGridState)
    } else {
        MainScreen.FolderDetail.ScrollToTopEffect(lazyListState)
    }

    LaunchedEffect(Unit) {
        viewModel.refreshFolderDetailTrigger.collectLatest {
            Timber.e("폴더 상세 리프레시")
            wishList.refresh()
        }
    }

    val selectedItemCount = if (isAllSelected) {
        (totalItemCount ?: wishList.itemCount) - excludedItemIds.size
    } else {
        selectedItemIds.size
    }

    FolderDetailScreen(
        wishItems = wishList,
        folderName = folderName,
        lazyGridState = lazyGridState,
        lazyListState = lazyListState,
        totalItemCount = totalItemCount,
        isSelectionMode = isSelectionMode,
        isAllSelected = isAllSelected,
        selectedItemIds = selectedItemIds,
        excludedItemIds = excludedItemIds,
        selectedItemCount = selectedItemCount,
        deleteSelectedItemsState = deleteSelectedItemsState,
        viewType = viewType,
        isExcludeOwnedItems = isExcludeOwnedItems,
        onClickItem = { id ->
            navController.navigate("${MainScreen.WishItemDetail.route}/$id")
        },
        onClickBack = navController::safePopBackStack,
        onClickToggleSelectionMode = viewModel::toggleSelectionMode,
        onClickToggleItemSelection = { id ->
            viewModel.toggleItemSelection(id, totalItemCount ?: wishList.itemCount)
        },
        onDragSelectItem = { id, isSelected ->
            viewModel.setItemSelected(id, isSelected, totalItemCount ?: wishList.itemCount)
        },
        onClickSelectAll = viewModel::toggleSelectAll,
        onClickDeleteSelected = { dialogData = DialogData.BulkWishItemDelete(selectedItemCount) },
        updateViewType = viewModel::updateViewType,
        updateExcludeOwnedItems = viewModel::updateExcludeOwnedItems,
    )

    WishBoardTwoButtonDialog(
        dialogData = dialogData,
        onClickConfirm = {
            val allLoadedItemIds = (0 until wishList.itemCount).mapNotNull { idx -> wishList[idx]?.id }
            viewModel.deleteSelectedItems(allLoadedItemIds)
        },
        onDismissRequest = { dialogData = null },
    )
}

@Composable
fun FolderDetailScreen(
    wishItems: LazyPagingItems<WishItem>,
    folderName: String,
    lazyGridState: LazyGridState,
    lazyListState: LazyListState,
    totalItemCount: Int?,
    isSelectionMode: Boolean,
    isAllSelected: Boolean,
    selectedItemIds: Set<Long>,
    excludedItemIds: Set<Long>,
    selectedItemCount: Int,
    deleteSelectedItemsState: WishBoardState<Unit>,
    viewType: WishListViewType,
    isExcludeOwnedItems: Boolean,
    onClickItem: (id: Long) -> Unit,
    onClickBack: () -> Unit,
    onClickToggleSelectionMode: () -> Unit,
    onClickToggleItemSelection: (id: Long) -> Unit,
    onDragSelectItem: (id: Long, isSelected: Boolean) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeleteSelected: () -> Unit,
    updateViewType: () -> Unit,
    updateExcludeOwnedItems: (Boolean) -> Unit,
) {
    // 전체 선택 상태에서는 excludedItemIds에 없는 아이템만 선택된 것으로 취급한다.
    val isItemSelected: (Long) -> Boolean = { id ->
        if (isAllSelected) !excludedItemIds.contains(id) else selectedItemIds.contains(id)
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                WishBoardTopBar(
                    topBarModel = WishBoardTopBarModel(
                        startIcon = WishBoardTopBarModel.TopBarIcon.CLOSE,
                        onClickStartIcon = onClickToggleSelectionMode,
                    ),
                )
            } else {
                WishBoardTopBar(
                    topBarModel = WishBoardTopBarModel(
                        title = folderName,
                        onClickStartIcon = onClickBack,
                    ),
                    endComponent = { modifier ->
                        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                            SelectionModeIconButton(onClick = onClickToggleSelectionMode)
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                    },
                )
            }
        },
        bottomBar = {
            if (isSelectionMode) {
                SelectionModeBottomBar(
                    selectedItemCount = selectedItemCount,
                    // excludedItemIds가 있으면 실제로는 전체 선택 상태가 아니므로 버튼엔 "전체 선택"이 노출돼야 한다.
                    isAllSelected = isAllSelected && excludedItemIds.isEmpty(),
                    onClickSelectAll = onClickSelectAll,
                    onClickDelete = onClickDeleteSelected,
                )
            }
        },
    ) { paddingValues ->
        val contentModifier = Modifier
            .fillMaxSize()
            .background(WishBoardTheme.colors.white)
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
            )

        if (deleteSelectedItemsState is WishBoardState.Loading) {
            Box(modifier = contentModifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = WishBoardTheme.colors.gray700)
            }
        } else {
            Column(modifier = contentModifier) {
                // 헤더 — 아이템이 없어도 항상 최상단에 노출
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "전체 ${totalItemCount ?: wishItems.itemCount}개",
                        style = WishBoardTheme.typography.suitD3,
                        color = WishBoardTheme.colors.gray200,
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SelectableCircleButton(
                            modifier = Modifier.padding(end = 5.dp),
                            isSelected = isExcludeOwnedItems,
                            onClick = { updateExcludeOwnedItems(!isExcludeOwnedItems) },
                        )

                        Text(
                            modifier = Modifier.padding(end = 10.dp),
                            text = "소장템 제외",
                            style = WishBoardTheme.typography.suitD3,
                            color = WishBoardTheme.colors.gray200,
                        )

                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .rippleClickable { updateViewType() },
                            painter = painterResource(id = viewType.iconRes),
                            contentDescription = viewType.description,
                            tint = Color.Unspecified,
                        )
                    }
                }

                if (
                    wishItems.itemCount == 0 &&
                    wishItems.loadState.refresh is LoadState.NotLoading &&
                    wishItems.loadState.append.endOfPaginationReached
                ) {
                    WishBoardEmptyView(
                        modifier = Modifier.fillMaxSize(),
                        guideTextRes = R.string.empty_wishlist_guide_text,
                    )
                } else if (viewType != WishListViewType.LIST) {
                    LazyVerticalGrid(
                        modifier = Modifier.dragToSelectItems(
                            gridState = lazyGridState,
                            enabled = isSelectionMode,
                            idAt = { idx -> wishItems[idx]?.id },
                            isSelected = isItemSelected,
                            onSelectedChange = onDragSelectItem,
                        ),
                        columns = GridCells.Fixed(if (viewType == WishListViewType.GRID_2_COLUMN) 2 else 3),
                        state = lazyGridState,
                    ) {
                        items(count = wishItems.itemCount, key = wishItems.itemKey { it.id }) { idx ->
                            val item = wishItems[idx]
                            item?.let {
                                WishItemForGridView(
                                    wishItem = item,
                                    isSelected = isItemSelected(item.id),
                                    onClickItem = {
                                        if (isSelectionMode) {
                                            onClickToggleItemSelection(item.id)
                                        } else {
                                            onClickItem(item.id)
                                        }
                                    },
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.dragToSelectItems(
                            listState = lazyListState,
                            enabled = isSelectionMode,
                            idAt = { idx -> wishItems[idx]?.id },
                            isSelected = isItemSelected,
                            onSelectedChange = onDragSelectItem,
                        ),
                        state = lazyListState,
                    ) {
                        items(count = wishItems.itemCount, key = wishItems.itemKey { it.id }) { idx ->
                            val item = wishItems[idx]
                            item?.let {
                                WishBoardDivider()
                                WishItemForListView(
                                    wishItem = item,
                                    isSelected = isItemSelected(item.id),
                                    onClickItem = {
                                        if (isSelectionMode) {
                                            onClickToggleItemSelection(item.id)
                                        } else {
                                            onClickItem(item.id)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewFolderDetailScreen() {
    val wishItem =
        WishItem(
            1L,
            "21SS SAGE SHIRT [4COLOR]",
            "https://url.kr/8vwf1e",
            108000,
        )

    FolderDetailScreen(
        wishItems = getFakePagingData(List(8) { index: Int -> wishItem.copy(id = index.toLong()) }),
        folderName = "상의",
        lazyGridState = rememberLazyGridState(),
        lazyListState = rememberLazyListState(),
        totalItemCount = 8,
        isSelectionMode = false,
        isAllSelected = false,
        selectedItemIds = emptySet(),
        excludedItemIds = emptySet(),
        selectedItemCount = 0,
        deleteSelectedItemsState = WishBoardState.Idle,
        viewType = WishListViewType.GRID_2_COLUMN,
        isExcludeOwnedItems = false,
        onClickItem = {},
        onClickBack = {},
        onClickToggleSelectionMode = {},
        onClickToggleItemSelection = {},
        onDragSelectItem = { _, _ -> },
        onClickSelectAll = {},
        onClickDeleteSelected = {},
        updateViewType = {},
        updateExcludeOwnedItems = {},
    )
}
