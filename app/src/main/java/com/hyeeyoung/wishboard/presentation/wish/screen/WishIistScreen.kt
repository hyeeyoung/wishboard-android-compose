package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.Calendar
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.WishItem
import com.hyeeyoung.wishboard.presentation.wish.WishListUiModel
import com.hyeeyoung.wishboard.presentation.wish.WishListViewModel
import com.hyeeyoung.wishboard.presentation.wish.component.WishItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishListScreen(navController: NavHostController, viewModel: WishListViewModel = hiltViewModel()) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = uiModel.isRefreshing,
        onRefresh = {
            viewModel.getWishItem()
        },
    ) {
        WishlistScreen(
            uiModel = uiModel,
            onClickCalendar = {
                navController.navigate(Calendar.route)
            },
            onClickWishItem = { id ->
                navController.navigate("${MainScreen.WishItemDetail.route}/${id}")
            }
        )
    }
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
fun WishlistTopBar(onClickCalendar: () -> Unit) { // TODO 공용 TopBar 추출
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.height(18.dp),
            painter = painterResource(id = R.drawable.ic_app_text_logo),
            contentDescription = null,
        )
        Row {
//            WishBoardIconButton(iconRes = R.drawable.ic_cart, onClick = { onClickCart() })
            WishBoardIconButton(iconRes = R.drawable.ic_calendar, onClick = { onClickCalendar() })
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
