package com.hyeeyoung.wishboard.presentation.wish.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.WishItem
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun WishItem(modifier: Modifier = Modifier, wishItem: WishItem, onClickItem: () -> Unit = {}) {
    Column(modifier = modifier.noRippleClickable { onClickItem() }) {
//        var cartState by remember { mutableStateOf(wishItem.isInCart) }
        // 이미지 및 장바구니 버튼
        Box {
            if (!wishItem.imageUrl.isNullOrBlank()) {
                ColoredImage(
                    model = wishItem.imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )
            } else {
                Box(
                    modifier = Modifier
                        .background(WishBoardTheme.colors.black.copy(alpha = 0.05f))
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    Row(modifier = Modifier.align(Alignment.Center)) {
                        Spacer(modifier = Modifier.weight(0.15f))
                        Icon(
                            modifier = Modifier.weight(0.7f),
                            painter = painterResource(id = R.drawable.ic_app_text_logo),
                            tint = WishBoardTheme.colors.gray150,
                            contentDescription = "이미지 없음"
                        )
                        Spacer(modifier = Modifier.weight(0.15f))
                    }
                }
            }

            /*Column(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomEnd),
            ) {
                CartButton(
                    isInCart = cartState,
                    changeCartState = { isInCart -> cartState = !isInCart },
                )
            }*/
        }

        // 상품명 및 가격
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 20.dp),
        ) {
            Text(
                text = wishItem.name,
                style = WishBoardTheme.typography.suitD3,
                color = WishBoardTheme.colors.gray700,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.size(8.dp))
            PriceText(
                price = wishItem.price ?: 0,
                priceStyle = WishBoardTheme.typography.montserratH3,
                wonStyle = WishBoardTheme.typography.suitD3,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff, widthDp = 187, heightDp = 257)
@Composable
fun PreviewWishItem() {
    WishItem(
        wishItem = WishItem(
            id = 1L,
            name = "21SS SAGE SHIRT [4COLOR]",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
//            true,
        ),
    )
}
