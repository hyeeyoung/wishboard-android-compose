package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.onboarding.OnboardingModalContent
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.wish.WishListViewModel
import com.hyeeyoung.wishboard.presentation.wish.component.WishItem
import com.hyeeyoung.wishboard.presentation.wish.model.WishListUiModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishListScreen(
    navController: NavHostController,
    isFirstLaunch: Boolean,
    viewModel: WishListViewModel = hiltViewModel()
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var isOpenOnboardingModal by remember { mutableStateOf(false) }
    val onboardingSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { newState ->
            newState != SheetValue.Hidden
        })

    LaunchedEffect(Unit) {
        viewModel.getWishItem()
    }

    LaunchedEffect(uiModel.shouldShowOnboardingModal) {
        if (uiModel.shouldShowOnboardingModal) {
            isOpenOnboardingModal = true
            onboardingSheetState.show()
        }
    }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    PullToRefreshBox(
        isRefreshing = uiModel.isRefreshing,
        onRefresh = {
            viewModel.getWishItem(true)
        },
    ) {
        WishlistScreen(
            uiModel = uiModel,
            onClickCalendar = {
                navController.navigate(MainScreen.Noti.route)
            },
            onClickWishItem = { id ->
                navController.navigate("${MainScreen.WishItemDetail.route}/${id}")
            }
        )
    }

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
                }
            )
        }
    )
}

@Composable
fun WishlistScreen(uiModel: WishListUiModel, onClickCalendar: () -> Unit, onClickWishItem: (id: Long) -> Unit) {
    Scaffold(topBar = {
        WishlistTopBar(onClickCalendar = onClickCalendar)
    }) { paddingValues ->
        val contentModifier = Modifier
            .fillMaxSize()
            .background(WishBoardTheme.colors.white)
            .padding(top = paddingValues.calculateTopPadding())

        if (uiModel.withItems.isEmpty()) {
            WishBoardEmptyView(
                modifier = contentModifier,
                guideTextRes = R.string.empty_wishlist_guide_text
            )
        } else {
            LazyVerticalGrid(
                modifier = contentModifier,
                columns = GridCells.Fixed(2),
            ) {
                items(uiModel.withItems) { wishItem ->
                    WishItem(
                        wishItem = wishItem,
                        onClickItem = {
                            onClickWishItem(wishItem.id)
                        },
                    )
                }
            }
        }
    }
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
            modifier = Modifier.height(18.dp),
            painter = painterResource(id = R.drawable.ic_app_text_logo),
            contentDescription = null,
        )
        Box(
            modifier = Modifier
                .noRippleClickable { onClickCalendar() }
                .padding(14.dp)
                .size(24.dp),
            contentAlignment = Alignment.Center
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
    WishlistScreen(
        uiModel = WishListUiModel(
            withItems = listOf(
                WishItem(
                    id = 1L,
                    name = "21SS SAGE SHIRT [4COLOR]",
                    imageUrl = "https://url.kr/8vwf1e",
                    price = 108000,
                ),
                WishItem(
                    id = 1L,
                    name = "SOFT BALL CHAIN MINI BAG [SILVER]",
                    imageUrl = "https://url.kr/8vwf1e",
                    price = 108000,
                ),
                WishItem(
                    id = 1L,
                    name = "썸머호텔 여름차렵이불세트",
                    imageUrl = "https://url.kr/8vwf1e",
                    price = 108000,
                ),
                WishItem(
                    id = 1L,
                    name = "Bean Ring Gold",
                    imageUrl = "https://url.kr/8vwf1e",
                    price = 108000,
                ),
                WishItem(
                    id = 1L,
                    name = "Bean Ring Gold",
                    imageUrl = "https://url.kr/8vwf1e",
                    price = 108000,
                ),
                WishItem(
                    id = 1L,
                    name = "Bean Ring Gold",
                    imageUrl = "https://url.kr/8vwf1e",
                    price = 108000,
                ),
            )
        ),
        onClickCalendar = {},
        onClickWishItem = {},
    )
}
