package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.BottomBarSelectionModeState
import com.hyeeyoung.wishboard.config.GlobalState
import com.hyeeyoung.wishboard.config.MoreOptionsState
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.loading.ThreeDotsLoadingView
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardTwoButtonDialog
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.presentation.onboarding.OnboardingModalContent
import com.hyeeyoung.wishboard.presentation.wish.model.WishListFilterType
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.WishBoardPullToRefreshBox
import com.hyeeyoung.wishboard.presentation.util.extension.dragToSelectItems
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rippleClickable
import com.hyeeyoung.wishboard.presentation.util.getFakeLoadingPagingData
import com.hyeeyoung.wishboard.presentation.util.getFakePagingData
import com.hyeeyoung.wishboard.presentation.wish.WishListViewModel
import com.hyeeyoung.wishboard.presentation.wish.component.WishItemForGridView
import com.hyeeyoung.wishboard.presentation.wish.component.WishItemForListView
import com.hyeeyoung.wishboard.presentation.wish.model.WishListUiModel
import com.hyeeyoung.wishboard.presentation.wish.model.WishListViewType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishListScreen(
    navController: NavHostController,
    isFirstLaunch: Boolean,
    viewModel: WishListViewModel = hiltViewModel(),
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val wishList = viewModel.wishList.collectAsLazyPagingItems()

    val coroutineScope = rememberCoroutineScope()
    var isOpenOnboardingModal by remember { mutableStateOf(false) }
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    val onboardingSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { newState ->
            newState != SheetValue.Hidden
        })
    val lazyGridState = rememberLazyGridState()
    val lazyListState = rememberLazyListState()

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    if (uiModel.viewType != WishListViewType.LIST) {
        MainScreen.Wishlist.ScrollToTopEffect(lazyGridState)
    } else {
        MainScreen.Wishlist.ScrollToTopEffect(lazyListState)
    }

    LaunchedEffect(Unit) {
        viewModel.refreshWishListTrigger.collectLatest {
            wishList.refresh()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.scrollToTopTrigger.collect {
            lazyGridState.scrollToItem(0)
            lazyListState.scrollToItem(0)
        }
    }

    LaunchedEffect(uiModel.shouldShowOnboardingModal) {
        if (uiModel.shouldShowOnboardingModal) {
            isOpenOnboardingModal = true
            onboardingSheetState.show()
        }
    }

    DisposableEffect(
        uiModel.isSelectionMode,
        uiModel.isAllSelected,
        uiModel.selectedItemIds,
        uiModel.excludedItemIds,
        uiModel.totalItemCount,
        wishList.itemCount,
    ) {
        GlobalState.bottomBarSelectionModeState.value = if (uiModel.isSelectionMode) {
            val selectedCount = if (uiModel.isAllSelected) {
                (uiModel.totalItemCount ?: 0) - uiModel.excludedItemIds.size
            } else {
                uiModel.selectedItemIds.size
            }

            val isItemSelected: (Long) -> Boolean = { id ->
                if (uiModel.isAllSelected) {
                    !uiModel.excludedItemIds.contains(id)
                } else {
                    uiModel.selectedItemIds.contains(id)
                }
            }
            val selectedItems = (0 until wishList.itemCount)
                .mapNotNull { wishList[it] }
                .filter { isItemSelected(it.id) }
            val allWish = selectedItems.isNotEmpty() && selectedItems.all {
                it.itemOwnershipStatus == WishItemOwnershipStatus.WISH
            }
            val allOwned = selectedItems.isNotEmpty() && selectedItems.all {
                it.itemOwnershipStatus == WishItemOwnershipStatus.OWNED
            }
            val ownedCountInSelected = selectedItems.count { it.itemOwnershipStatus == WishItemOwnershipStatus.OWNED }
            val wishCountInSelected = selectedItems.count { it.itemOwnershipStatus == WishItemOwnershipStatus.WISH }

            BottomBarSelectionModeState(
                selectedItemCount = selectedCount,
                // excludedItemIds가 있으면 실제로는 전체 선택 상태가 아니므로 버튼엔 "전체 선택"이 노출돼야 한다.
                isAllSelected = uiModel.isAllSelected && uiModel.excludedItemIds.isEmpty(),
                onClickSelectAll = viewModel::toggleSelectAll,
                onClickDelete = { dialogData = DialogData.BulkWishItemDelete(selectedCount) },
                moreOptions = MoreOptionsState(
                    showConvertToWish = !allWish,
                    showConvertToOwned = !allOwned,
                    // 위시템으로 전환: 소장템 아이템 수 표시
                    onClickConvertToWish = { dialogData = DialogData.BulkWishItemToWish(ownedCountInSelected) },
                    // 소장템으로 전환: 위시템 아이템 수 표시
                    onClickConvertToOwned = { dialogData = DialogData.BulkWishItemToOwned(wishCountInSelected) },
                ),
            )
        } else {
            null
        }

        onDispose { GlobalState.bottomBarSelectionModeState.value = null }
    }

    WishlistScreen(
        uiModel = uiModel,
        wishList = wishList,
        lazyGridState = lazyGridState,
        lazyListState = lazyListState,
        onClickCalendar = {
            navController.navigate(MainScreen.Noti.route)
        },
        onClickWishItem = { id ->
            navController.navigate("${MainScreen.WishItemDetail.route}/$id")
        },
        updateViewType = viewModel::updateViewType,
        updateFilterType = viewModel::updateFilterType,
        dismissBulkRegisterBanner = viewModel::dismissBulkRegisterBanner,
        onClickToggleSelectionMode = viewModel::toggleSelectionMode,
        onClickToggleItemSelection = viewModel::toggleItemSelection,
        onDragSelectItem = viewModel::setItemSelected,
        onClickBanner = {
            navController.navigate(MainScreen.BulkRegisterWebView.route)
        },
        onRefresh = {
            wishList.refresh()
        },
    )

    WishBoardModal(
        isOpen = isOpenOnboardingModal,
        sheetState = onboardingSheetState,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        onDismissRequest = {
            isOpenOnboardingModal = false
            viewModel.updateOnboardingModalStatus(isOnboardingComplete = false)
        },
        content = {
            OnboardingModalContent(
                onClickConfirm = {
                    isOpenOnboardingModal = false
                    viewModel.updateOnboardingModalStatus(isOnboardingComplete = true)
                    coroutineScope.launch {
                        onboardingSheetState.hide()
                    }
                },
            )
        },
    )

    WishBoardTwoButtonDialog(
        dialogData = dialogData,
        onClickConfirm = {
            val isItemSelected: (Long) -> Boolean = { id ->
                if (uiModel.isAllSelected) {
                    !uiModel.excludedItemIds.contains(id)
                } else {
                    uiModel.selectedItemIds.contains(id)
                }
            }
            val loadedItems = (0 until wishList.itemCount).mapNotNull { wishList[it] }

            when (dialogData) {
                is DialogData.BulkWishItemToWish -> {
                    // 선택된 아이템 중 소장템(OWNED)만 전환 대상
                    val targetIds = loadedItems
                        .filter { isItemSelected(it.id) && it.itemOwnershipStatus == WishItemOwnershipStatus.OWNED }
                        .map { it.id }
                    viewModel.convertSelectedItems(
                        targetStatus = WishItemOwnershipStatus.WISH,
                        itemIds = targetIds,
                    )
                }
                is DialogData.BulkWishItemToOwned -> {
                    // 선택된 아이템 중 위시템(WISH)만 전환 대상
                    val targetIds = loadedItems
                        .filter { isItemSelected(it.id) && it.itemOwnershipStatus == WishItemOwnershipStatus.WISH }
                        .map { it.id }
                    viewModel.convertSelectedItems(
                        targetStatus = WishItemOwnershipStatus.OWNED,
                        itemIds = targetIds,
                    )
                }
                else -> {
                    val allLoadedItemIds = loadedItems.map { it.id }
                    viewModel.deleteSelectedItems(allLoadedItemIds)
                }
            }
        },
        onDismissRequest = { dialogData = null },
    )
}

@Composable
fun WishlistScreen(
    uiModel: WishListUiModel,
    wishList: LazyPagingItems<WishItem>,
    lazyGridState: LazyGridState,
    lazyListState: LazyListState,
    onClickCalendar: () -> Unit,
    onClickWishItem: (id: Long) -> Unit,
    updateViewType: () -> Unit,
    updateFilterType: (WishListFilterType) -> Unit,
    dismissBulkRegisterBanner: () -> Unit,
    onClickBanner: () -> Unit,
    onRefresh: () -> Unit,
    onClickToggleSelectionMode: () -> Unit,
    onClickToggleItemSelection: (id: Long) -> Unit,
    onDragSelectItem: (id: Long, isSelected: Boolean) -> Unit,
) {
    var isFilterSheetOpen by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var topBarHeightPx by remember { mutableFloatStateOf(with(density) { 52.dp.toPx() }) }
    var topBarOffsetPx by remember { mutableFloatStateOf(0f) }

    // 최초 진입 시의 로딩만 전체 화면 로딩뷰로 보여주고, 이후 당겨서 새로고침할 때는 보여주지 않는다.
    var hasLoadedOnce by remember { mutableStateOf(false) }
    LaunchedEffect(wishList.loadState.refresh) {
        if (wishList.loadState.refresh !is LoadState.Loading) {
            hasLoadedOnce = true
        }
    }
    val isFirstLoading = !hasLoadedOnce && wishList.loadState.refresh is LoadState.Loading

    // 전체 선택 상태에서는 excludedItemIds에 없는 아이템만 선택된 것으로 취급한다.
    val isItemSelected: (Long) -> Boolean = { id ->
        if (uiModel.isAllSelected) !uiModel.excludedItemIds.contains(id) else uiModel.selectedItemIds.contains(id)
    }

    // 선택모드의 X버튼 탑바는 스크롤에 영향받지 않고 항상 상단에 고정되어야 한다.
    val isSelectionModeState = rememberUpdatedState(uiModel.isSelectionMode)
    LaunchedEffect(uiModel.isSelectionMode) {
        if (uiModel.isSelectionMode) topBarOffsetPx = 0f
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isSelectionModeState.value) return Offset.Zero

                topBarOffsetPx = (topBarOffsetPx + available.y).coerceIn(-topBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding())
                .nestedScroll(nestedScrollConnection),
        ) {
            val topBarHeightDp = with(density) { topBarHeightPx.toDp() }
            val topBarOffsetDp = with(density) { topBarOffsetPx.toDp() }
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(top = topBarHeightDp + topBarOffsetDp)

            WishBoardPullToRefreshBox(
                loadState = wishList.loadState.refresh,
                onRefresh = onRefresh,
            ) {
                when {
                    uiModel.deleteSelectedItemsState is WishBoardState.Loading -> {
                        Box(modifier = contentModifier, contentAlignment = Alignment.Center) {
                            ThreeDotsLoadingView()
                        }
                    }

                    else -> {
                        Column(modifier = contentModifier) {
                            // 스티키 헤더 — 리스트 밖에 위치하므로 아이템이 없어도 항상 최상단에 노출
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier.alpha(if (uiModel.totalItemCount != null) 1f else 0f),
                                    text = "전체 ${uiModel.totalItemCount ?: 0}개",
                                    style = WishBoardTheme.typography.suitD3,
                                    color = WishBoardTheme.colors.gray200,
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    WishListFilterChip(
                                        filterType = uiModel.filterType,
                                        onClick = { isFilterSheetOpen = true },
                                    )

                                    Icon(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .rippleClickable { updateViewType() },
                                        painter = painterResource(id = uiModel.viewType.iconRes),
                                        contentDescription = uiModel.viewType.description,
                                        tint = Color.Unspecified,
                                    )
                                }
                            }

                            if (isFirstLoading) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    ThreeDotsLoadingView()
                                }
                            } else if (wishList.itemCount == 0 &&
                                wishList.loadState.refresh is LoadState.NotLoading &&
                                wishList.loadState.append.endOfPaginationReached
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    item {
                                        WishBoardEmptyView(
                                            modifier = Modifier.fillMaxWidth(),
                                            guideTextRes = R.string.empty_wishlist_guide_text,
                                        )
                                    }
                                }
                            } else if (uiModel.viewType != WishListViewType.LIST) {
                                LazyVerticalGrid(
                                    modifier = Modifier.dragToSelectItems(
                                        gridState = lazyGridState,
                                        enabled = uiModel.isSelectionMode,
                                        idAt = { idx -> wishList[idx]?.id },
                                        isSelected = isItemSelected,
                                        onSelectedChange = onDragSelectItem,
                                    ),
                                    columns = GridCells.Fixed(
                                        if (uiModel.viewType == WishListViewType.GRID_2_COLUMN) 2 else 3,
                                    ),
                                    state = lazyGridState,
                                ) {
                                    items(count = wishList.itemCount, key = { idx -> idx }) { idx ->
                                        val item = wishList[idx]
                                        item?.let {
                                            WishItemForGridView(
                                                wishItem = it,
                                                isSelected = isItemSelected(it.id),
                                                onClickItem = {
                                                    if (uiModel.isSelectionMode) {
                                                        onClickToggleItemSelection(it.id)
                                                    } else {
                                                        onClickWishItem(it.id)
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
                                        enabled = uiModel.isSelectionMode,
                                        idAt = { idx -> wishList[idx]?.id },
                                        isSelected = isItemSelected,
                                        onSelectedChange = onDragSelectItem,
                                    ),
                                    state = lazyListState,
                                ) {
                                    items(count = wishList.itemCount, key = { idx -> idx }) { idx ->
                                        val item = wishList[idx]
                                        item?.let {
                                            WishBoardDivider()
                                            WishItemForListView(
                                                wishItem = item,
                                                isSelected = isItemSelected(item.id),
                                                onClickItem = {
                                                    if (uiModel.isSelectionMode) {
                                                        onClickToggleItemSelection(item.id)
                                                    } else {
                                                        onClickWishItem(item.id)
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

            // 배너 + TopBar — 스크롤 시 함께 접힘
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = topBarOffsetPx }
                    .onSizeChanged { topBarHeightPx = it.height.toFloat() },
            ) {
                Column {
                    AnimatedVisibility(
                        visible = uiModel.isBulkRegisterBannerVisible && !uiModel.isSelectionMode,
                        exit = shrinkVertically(animationSpec = tween(300)) +
                            slideOutVertically(animationSpec = tween(300)) { -it },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WishBoardTheme.colors.gray700)
                                .rippleClickable { onClickBanner() }
                                .padding(vertical = 10.dp)
                                .padding(start = 16.dp, end = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "기존 위시리스트를 위시보드로 옮겨보세요 📦",
                                style = WishBoardTheme.typography.suitD2,
                                color = WishBoardTheme.colors.white,
                            )
                            Icon(
                                modifier = Modifier
                                    .size(16.dp)
                                    .noRippleClickable { dismissBulkRegisterBanner() },
                                painter = painterResource(R.drawable.ic_close),
                                tint = WishBoardTheme.colors.gray200,
                                contentDescription = "",
                            )
                        }
                    }
                    Box(modifier = Modifier.background(WishBoardTheme.colors.white)) {
                        WishlistTopBar(
                            isSelectionMode = uiModel.isSelectionMode,
                            onClickCalendar = onClickCalendar,
                            onClickToggleSelectionMode = onClickToggleSelectionMode,
                        )
                    }
                }
            }
        }
    }

    WishBoardModal(
        isOpen = isFilterSheetOpen,
        titleRes = R.string.wishlist_filter_title,
        onDismissRequest = { isFilterSheetOpen = false },
        content = {
            WishListFilterSheetContent(
                selectedFilterType = uiModel.filterType,
                onSelectFilterType = { filterType ->
                    updateFilterType(filterType)
                    isFilterSheetOpen = false
                },
            )
        },
    )
}

@Composable
fun WishListFilterChip(
    filterType: WishListFilterType,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF3F3F3))
            .rippleClickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = filterType.label,
            style = WishBoardTheme.typography.suitB5,
            color = WishBoardTheme.colors.gray600,
        )
        Icon(
            modifier = Modifier.size(8.dp, 5.dp),
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

@Composable
fun WishListFilterSheetContent(
    selectedFilterType: WishListFilterType,
    onSelectFilterType: (WishListFilterType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 64.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        WishListFilterRow.entries.forEach { row ->
            WishListFilterSheetRow(
                label = row.label,
                isSelected = selectedFilterType == row.filterType,
                onClick = { onSelectFilterType(row.filterType) },
            )
            if (row != WishListFilterRow.entries.last()) {
                WishBoardDivider()
            }
        }
    }
}

private enum class WishListFilterRow(val filterType: WishListFilterType, val label: String) {
    ALL(WishListFilterType.ALL, "전체 보기"),
    OWNED(WishListFilterType.OWNED, "소장템만 보기"),
    WISH(WishListFilterType.WISH, "위시템만 보기"),
}

@Composable
private fun WishListFilterSheetRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .rippleClickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = WishBoardTheme.typography.suitB3,
            color = if (isSelected) WishBoardTheme.colors.gray700 else WishBoardTheme.colors.gray300,
        )
        FilterCheckCircle(isSelected = isSelected)
    }
}

@Composable
private fun FilterCheckCircle(isSelected: Boolean) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(WishBoardTheme.colors.green700),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(11.dp),
                    painter = painterResource(R.drawable.ic_check_white),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(1.dp, WishBoardTheme.colors.gray150, CircleShape),
            )
        }
    }
}

@Composable
fun SelectableCircleButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val iconRes = if (isSelected) {
        R.drawable.ic_circle_selected
    } else {
        R.drawable.ic_circle_unselected
    }

    Icon(
        modifier = modifier
            .size(14.dp)
            .clip(CircleShape)
            .rippleClickable { onClick() },
        painter = painterResource(id = iconRes),
        contentDescription = if (isSelected) "선택" else "선택 해제",
        tint = Color.Unspecified,
    )
}

@Composable
fun WishlistTopBar(
    isSelectionMode: Boolean,
    onClickCalendar: () -> Unit,
    onClickToggleSelectionMode: () -> Unit,
) {
    if (isSelectionMode) {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                startIcon = WishBoardTopBarModel.TopBarIcon.CLOSE,
                onClickStartIcon = onClickToggleSelectionMode,
            ),
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.height(19.dp),
                painter = painterResource(id = R.drawable.ic_app_text_logo),
                contentDescription = null,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                WishBoardIconButton(
                    size = 42.dp,
                    iconRes = R.drawable.ic_main_top_bar_check,
                    contentDescription = "아이템 선택",
                    onClick = onClickToggleSelectionMode,
                )

                WishBoardIconButton(
                    size = 42.dp,
                    iconRes = R.drawable.ic_main_top_bar_noti,
                    contentDescription = "알림",
                    onClick = onClickCalendar,
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewWishlistScreen() {
    val wishItems = listOf(
        WishItem(
            id = 1L,
            name = "21SS SAGE SHIRT [4COLOR]",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
            itemOwnershipStatus = WishItemOwnershipStatus.WISH,
        ),
        WishItem(
            id = 1L,
            name = "SOFT BALL CHAIN MINI BAG [SILVER]",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
            itemOwnershipStatus = WishItemOwnershipStatus.OWNED,
        ),
        WishItem(
            id = 1L,
            name = "썸머호텔 여름차렵이불세트",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
            itemOwnershipStatus = WishItemOwnershipStatus.WISH,
        ),
        WishItem(
            id = 1L,
            name = "Bean Ring Gold",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
            itemOwnershipStatus = WishItemOwnershipStatus.OWNED,
        ),
        WishItem(
            id = 1L,
            name = "Bean Ring Gold",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
            itemOwnershipStatus = WishItemOwnershipStatus.WISH,
        ),
        WishItem(
            id = 1L,
            name = "Bean Ring Gold",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
            itemOwnershipStatus = WishItemOwnershipStatus.WISH,
        ),
    )

    WishlistScreen(
        uiModel = WishListUiModel(
            isSelectionMode = false,
            totalItemCount = wishItems.size,
        ),
        lazyGridState = rememberLazyGridState(),
        lazyListState = rememberLazyListState(),
        wishList = getFakePagingData(wishItems),
        onClickCalendar = {},
        onClickWishItem = {},
        updateViewType = {},
        updateFilterType = {},
        dismissBulkRegisterBanner = {},
        onClickBanner = {},
        onRefresh = {},
        onClickToggleSelectionMode = {},
        onClickToggleItemSelection = {},
        onDragSelectItem = { _, _ -> },
    )
}

@Composable
@Preview
fun PreviewWishlistScreenFirstLoading() {
    WishlistScreen(
        uiModel = WishListUiModel(),
        lazyGridState = rememberLazyGridState(),
        lazyListState = rememberLazyListState(),
        wishList = getFakeLoadingPagingData(),
        onClickCalendar = {},
        onClickWishItem = {},
        updateViewType = {},
        updateFilterType = {},
        dismissBulkRegisterBanner = {},
        onClickBanner = {},
        onRefresh = {},
        onClickToggleSelectionMode = {},
        onClickToggleItemSelection = {},
        onDragSelectItem = { _, _ -> },
    )
}
