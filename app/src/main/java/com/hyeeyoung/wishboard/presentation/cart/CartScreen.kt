package com.hyeeyoung.wishboard.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.CartItem
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.wish.component.PriceText

@Composable
fun CartScreen(cartItems: List<CartItem> = emptyList()) {
    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(title = stringResource(id = R.string.cart)),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                itemsIndexed(cartItems) { idx, item ->
                    CartItem(cartItem = item)
                    if (idx < cartItems.lastIndex) WishBoardDivider()
                }
            }

            CartTotalDisplay(totalCount = cartItems.size, totalPrice = 180000)
        }
    }
}

@Composable
fun CartItem(cartItem: CartItem) {
    val imageSize = 84
    Row(verticalAlignment = Alignment.CenterVertically) {
        ColoredImage(
            model = cartItem.image,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .size(imageSize.dp)
                .clip(RoundedCornerShape(10.dp)),
        )
        Column(modifier = Modifier.height((imageSize + 32).dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp, top = 16.dp),
                    text = cartItem.name,
                    style = WishBoardTheme.typography.suitD2M,
                    color = WishBoardTheme.colors.gray700,
                )
                Surface(modifier = Modifier.padding(top = 6.dp, end = 4.dp)) {
                    WishBoardIconButton(iconRes = R.drawable.ic_delete_small_gray, onClick = { /*TODO*/ })
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                ItemCountController(cartItem.count)
                PriceText(
                    modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                    price = cartItem.price,
                    priceStyle = WishBoardTheme.typography.montserratH2,
                    wonStyle = WishBoardTheme.typography.suitD2,
                )
            }
        }
    }
}

@Composable
fun ItemCountController(count: Int) {
    Row(modifier = Modifier.padding(start = 2.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        WishBoardIconButton(iconRes = R.drawable.ic_cart_minus, onClick = { /*TODO*/ })
        Text(modifier = Modifier.width(18.dp), text = count.toString(), textAlign = TextAlign.Center)
        WishBoardIconButton(iconRes = R.drawable.ic_cart_plus, onClick = { /*TODO*/ })
    }
}

@Composable
fun CartTotalDisplay(totalCount: Int, totalPrice: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WishBoardTheme.colors.green500)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.padding(end = 2.dp),
                text = stringResource(id = R.string.total),
                style = WishBoardTheme.typography.suitD2,
                color = WishBoardTheme.colors.gray700,
            )
            Text(
                text = "$totalCount",
                style = WishBoardTheme.typography.montserratH2,
                color = WishBoardTheme.colors.gray700,
            )
            Text(
                modifier = Modifier.padding(start = 2.dp),
                text = stringResource(id = R.string.ea),
                style = WishBoardTheme.typography.suitD2,
                color = WishBoardTheme.colors.gray700,
            )
        }

        PriceText(
            price = totalPrice,
            priceStyle = WishBoardTheme.typography.montserratH2,
            wonStyle = WishBoardTheme.typography.suitD2,
        )
    }
}

@Composable
@Preview
fun PreviewCartScreen() {
    val cartItem = listOf(
        CartItem(
            id = 1L,
            name = "Bean Ring Gold",
            image = "https://url.kr/8vwf1e",
            price = 108000,
            count = 1,
        ),
    )
    val cartItems = List(7) { cartItem }.flatten()

    CartScreen(
        cartItems = cartItems,
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCartItem() {
    val cartItem = CartItem(
        id = 1L,
        name = "Bean Ring Gold",
        image = "https://url.kr/8vwf1e",
        price = 108000,
        1,
    )

    Column() {
        CartItem(
            cartItem = cartItem,
        )
        CartItem(
            cartItem = cartItem.copy(count = 88),
        )
    }
}
