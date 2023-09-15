package com.hyeeyoung.wishboard.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.WishBoardDialog
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.Green500
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.CartItem
import com.hyeeyoung.wishboard.presentation.model.WishBoardDialogTextRes
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.wish.component.PriceText

@Composable
fun CartScreen(navController: NavHostController) {
    // TODO 서버 연동 시 삭제
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
    val systemUiController = rememberSystemUiController()
    var isOpenDialog by remember { mutableStateOf(false) }

    SideEffect {
        systemUiController.setNavigationBarColor(color = Green500)
    }

    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                title = stringResource(id = R.string.cart),
                onClickStartIcon = { navController.popBackStack() },
            ),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            if (cartItems.isEmpty()) {
                WishBoardEmptyView(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally),
                    guideTextRes = R.string.empty_cart_guide_text,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                ) {
                    itemsIndexed(cartItems) { idx, item ->
                        CartItem(
                            cartItem = item,
                            moveToDetail = { id -> navController.navigate("${MainScreen.WishItemDetail.route}/$id") },
                            onChangeItemCount = { count -> /*TODO*/ },
                            onClickDelete = { id -> isOpenDialog = true },
                        )
                        if (idx < cartItems.lastIndex) WishBoardDivider()
                    }
                }
            }
            CartTotalDisplay(totalCount = cartItems.size, totalPrice = cartItems.sumOf { it.price * it.count })
        }

        WishBoardDialog(
            isOpen = isOpenDialog,
            textRes = WishBoardDialogTextRes(
                titleRes = R.string.dialog_cart_title,
                descriptionRes = R.string.dialog_cart_description,
                dismissBtnTextRes = R.string.cancel,
                confirmBtnTextRes = R.string.delete,
            ),
            onClickConfirm = {},
            onDismissRequest = { isOpenDialog = false },
        )
    }
}

@Composable
fun CartItem(
    cartItem: CartItem,
    moveToDetail: (Long) -> Unit = {},
    onChangeItemCount: (Int) -> Unit = {},
    onClickDelete: (Long) -> Unit = {},
) {
    val imageSize = 84
    Row(verticalAlignment = Alignment.CenterVertically) {
        ColoredImage(
            model = cartItem.image,
            modifier = Modifier
                .noRippleClickable { moveToDetail(cartItem.id) }
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .size(imageSize.dp)
                .clip(RoundedCornerShape(10.dp)),
        )
        Column(modifier = Modifier.height((imageSize + 32).dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Text(
                    modifier = Modifier
                        .noRippleClickable { moveToDetail(cartItem.id) }
                        .weight(1f)
                        .padding(start = 10.dp, top = 16.dp),
                    text = cartItem.name,
                    style = WishBoardTheme.typography.suitD2M,
                    color = WishBoardTheme.colors.gray700,
                )

                Surface(modifier = Modifier.padding(top = 6.dp, end = 4.dp)) {
                    WishBoardIconButton(
                        modifier = Modifier.background(WishBoardTheme.colors.white),
                        iconRes = R.drawable.ic_delete_small_gray,
                        onClick = { onClickDelete(cartItem.id) },
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                ItemCountController(count = cartItem.count, onChangeItemCount = { count -> onChangeItemCount(count) })
                PriceText(
                    modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                    price = cartItem.price * cartItem.count,
                    priceStyle = WishBoardTheme.typography.montserratH2,
                    wonStyle = WishBoardTheme.typography.suitD2,
                )
            }
        }
    }
}

@Composable
fun ItemCountController(count: Int, onChangeItemCount: (Int) -> Unit) {
    Row(modifier = Modifier.padding(start = 2.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        WishBoardIconButton(iconRes = R.drawable.ic_cart_minus, onClick = { onChangeItemCount(-1) })
        Text(modifier = Modifier.width(18.dp), text = count.toString(), textAlign = TextAlign.Center)
        WishBoardIconButton(iconRes = R.drawable.ic_cart_plus, onClick = { onChangeItemCount(+1) })
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
    CartScreen(navController = rememberNavController())
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
