package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.paging.compose.itemKey
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.presentation.onboarding.OnboardingModalContent
import com.hyeeyoung.wishboard.presentation.util.WishBoardPullToRefreshBox
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rippleClickable
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
            viewModel.fetchWishItemCount()
        }
    }

    LaunchedEffect(uiModel.shouldShowOnboardingModal) {
        if (uiModel.shouldShowOnboardingModal) {
            isOpenOnboardingModal = true
            onboardingSheetState.show()
        }
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
        updateExcludeOwnedItems = viewModel::updateExcludeOwnedItems,
        dismissBulkRegisterBanner = viewModel::dismissBulkRegisterBanner,
        onRefresh = {
            wishList.refresh()
            viewModel.fetchWishItemCount()
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
    updateExcludeOwnedItems: (Boolean) -> Unit,
    dismissBulkRegisterBanner: () -> Unit,
    onRefresh: () -> Unit,
) {
    val density = LocalDensity.current
    var topBarHeightPx by remember { mutableFloatStateOf(with(density) { 52.dp.toPx() }) }
    var topBarOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
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
                    wishList.itemCount == 0 &&
                        wishList.loadState.refresh is LoadState.NotLoading &&
                        wishList.loadState.append.endOfPaginationReached -> {
                        LazyColumn(
                            modifier = contentModifier,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            item {
                                WishBoardEmptyView(
                                    modifier = contentModifier,
                                    guideTextRes = R.string.empty_wishlist_guide_text,
                                )
                            }
                        }
                    }

                    else -> {
                        Column(modifier = contentModifier) {
                            // 스티키 헤더 — 리스트 밖에 위치하므로 항상 최상단에 고정
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val total = uiModel.wishItemCount?.totalCount ?: 0
                                val filteredTotal = if (!uiModel.isExcludeOwnedItems) {
                                    total
                                } else {
                                    total - (uiModel.wishItemCount?.ownedCount ?: 0)
                                }

                                Text(
                                    modifier = Modifier.alpha(
                                        if (
                                            uiModel.wishItemCount?.totalCount != null
                                        ) {
                                            1f
                                        } else {
                                            0f
                                        },
                                    ),
                                    text = "전체 ${filteredTotal}개",
                                    style = WishBoardTheme.typography.suitD3,
                                    color = WishBoardTheme.colors.gray200,
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    uiModel.wishItemCount?.ownedCount?.let {
                                        SelectableCircleButton(
                                            modifier = Modifier.padding(end = 5.dp),
                                            isSelected = uiModel.isExcludeOwnedItems,
                                            onClick = { updateExcludeOwnedItems(!uiModel.isExcludeOwnedItems) },
                                        )

                                        Text(
                                            modifier = Modifier.padding(end = 10.dp),
                                            text = "소장템 제외",
                                            style = WishBoardTheme.typography.suitD3,
                                            color = WishBoardTheme.colors.gray200,
                                        )
                                    }

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

                            if (uiModel.viewType != WishListViewType.LIST) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(
                                        if (uiModel.viewType == WishListViewType.GRID_2_COLUMN) 2 else 3,
                                    ),
                                    state = lazyGridState,
                                ) {
                                    items(count = wishList.itemCount, key = wishList.itemKey { it.id }) { idx ->
                                        val item = wishList[idx]
                                        item?.let {
                                            WishItemForGridView(
                                                wishItem = it,
                                                onClickItem = { onClickWishItem(it.id) },
                                            )
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(state = lazyListState) {
                                    items(count = wishList.itemCount, key = wishList.itemKey { it.id }) { idx ->
                                        val item = wishList[idx]
                                        item?.let {
                                            WishBoardDivider()
                                            WishItemForListView(
                                                wishItem = item,
                                                onClickItem = { onClickWishItem(item.id) },
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
                        visible = uiModel.isBulkRegisterBannerVisible,
                        exit = shrinkVertically(animationSpec = tween(300)) +
                            slideOutVertically(animationSpec = tween(300)) { -it },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WishBoardTheme.colors.gray700)
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
                        WishlistTopBar(onClickCalendar = onClickCalendar)
                    }
                }
            }
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
fun WishlistTopBar(onClickCalendar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.height(19.dp),
            painter = painterResource(id = R.drawable.ic_app_text_logo),
            contentDescription = null,
        )
        Box(
            modifier = Modifier
                .noRippleClickable { onClickCalendar() }
                .padding(14.dp)
                .size(24.dp),
            contentAlignment = Alignment.Center,
        ) {
//            WishBoardIconButton(iconRes = R.drawable.ic_cart, onClick = { onClickCart() })

            Icon(
                painter = painterResource(id = R.drawable.ic_notice),
                contentDescription = "알림",
                tint = Color.Unspecified,
            )
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
        uiModel = WishListUiModel(),
        lazyGridState = rememberLazyGridState(),
        lazyListState = rememberLazyListState(),
        wishList = getFakePagingData(wishItems),
        onClickCalendar = {},
        onClickWishItem = {},
        updateViewType = {},
        updateExcludeOwnedItems = {},
        dismissBulkRegisterBanner = {},
        onRefresh = {},
    )
}
