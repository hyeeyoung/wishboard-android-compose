package com.hyeeyoung.wishboard.presentation.wish

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.Calendar
import com.hyeeyoung.wishboard.config.navigation.screen.Cart
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.domain.model.WishItem
import com.hyeeyoung.wishboard.presentation.wish.component.WishItem

@Composable
fun WishlistScreen(navController: NavHostController) {
    val wishList = listOf(
        WishItem(
            1L,
            "21SS SAGE SHIRT [4COLOR]",
            "https://url.kr/8vwf1e",
            108000,
            true,
        ),
        WishItem(
            1L,
            "SOFT BALL CHAIN MINI BAG [SILVER]",
            "https://url.kr/8vwf1e",
            108000,
            false,
        ),
        WishItem(
            1L,
            "썸머호텔 여름차렵이불세트",
            "https://url.kr/8vwf1e",
            108000,
            false,
        ),
        WishItem(
            1L,
            "Bean Ring Gold",
            "https://url.kr/8vwf1e",
            108000,
            true,
        ),
        WishItem(
            1L,
            "Bean Ring Gold",
            "https://url.kr/8vwf1e",
            108000,
            false,
        ),
        WishItem(
            1L,
            "Bean Ring Gold",
            "https://url.kr/8vwf1e",
            108000,
            false,
        ),
    )

    WishboardTheme {
        Scaffold(topBar = {
            WishlistTopBar(onClickCart = { navController.navigate(Cart.route) }, onClickCalendar = {
                navController.navigate(Calendar.route)
            })
        }) { paddingValues ->
            val contentModifier = Modifier
                .fillMaxSize()
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding())

            if (wishList.isEmpty()) {
                WishBoardEmptyView(modifier = contentModifier, guideTextRes = R.string.empty_wishlist_guide_text)
            } else {
                LazyVerticalGrid(
                    modifier = contentModifier,
                    columns = GridCells.Fixed(2),
                ) {
                    items(wishList) { wishItem ->
                        WishItem(
                            wishItem = wishItem,
                            onClickItem = {
                                navController.navigate("${MainScreen.WishItemDetail.route}/${wishItem.id}")
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistTopBar(onClickCart: () -> Unit, onClickCalendar: () -> Unit) { // TODO 공용 TopBar 추출
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
        Row() {
            WishBoardIconButton(iconRes = R.drawable.ic_cart, onClick = { onClickCart() })
            WishBoardIconButton(iconRes = R.drawable.ic_calendar, onClick = { onClickCalendar() })
        }
    }
}

@Composable
@Preview
fun PreviewWishlistScreen() {
    WishlistScreen(navController = rememberNavController())
}
