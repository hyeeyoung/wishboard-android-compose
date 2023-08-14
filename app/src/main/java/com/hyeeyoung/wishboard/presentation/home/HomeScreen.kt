package com.hyeeyoung.wishboard.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.CartButton
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.domain.WishItem
import com.hyeeyoung.wishboard.util.extension.setPriceFormat

@Composable
fun HomeScreen(modifier: Modifier) {
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
    WishboardTheme { // TODO Theme 사용 여부 고려
        Surface(modifier = modifier, color = WishBoardTheme.colors.white) {
            Scaffold(topBar = { HomeTopBar() }) { paddingValues ->
                LazyVerticalGrid(
                    modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                    columns = GridCells.Fixed(2),
                ) {
                    items(wishList) { wishItem -> WishItemComponent(wishItem = wishItem) }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar() {
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
            WishBoardIconButton(iconRes = R.drawable.ic_cart, {})
            WishBoardIconButton(iconRes = R.drawable.ic_calendar, {})
        }
    }
}

@Composable
fun WishItemComponent(wishItem: WishItem) {
    Column {
        var cartState by remember { mutableStateOf(wishItem.isInCart) }
        // 이미지 및 장바구니 버튼
        Box {
            ColoredImage(
                model = wishItem.imageUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
            )

            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomEnd),
            ) {
                CartButton(
                    isInCart = cartState,
                    changeCartState = { isInCart -> cartState = !isInCart },
                )
            }
        }

        // 상품명 및 가격
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 20.dp),
        ) {
            Text(text = wishItem.name, style = WishBoardTheme.typography.suitD3, color = WishBoardTheme.colors.gray700)
            Spacer(modifier = Modifier.size(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = wishItem.price.setPriceFormat(),
                    style = WishBoardTheme.typography.montserratH3,
                    color = WishBoardTheme.colors.gray700,
                )
                Text(
                    text = stringResource(id = R.string.won),
                    style = WishBoardTheme.typography.suitD3,
                    color = WishBoardTheme.colors.gray700,
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewHomeScreen() {
    HomeScreen(Modifier)
}
