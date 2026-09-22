package com.hyeeyoung.wishboard.presentation.wish.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.image.WishBoardInitialPlaceHolder
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun WishItemForGridView(
    modifier: Modifier = Modifier,
    wishItem: WishItem,
    isSelected: Boolean = false,
    onClickItem: () -> Unit = {},
) {
    val imageModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
//        var cartState by remember { mutableStateOf(wishItem.isInCart) }

    Column(modifier = modifier.noRippleClickable { onClickItem() }) {
        // 이미지
        Box {
            Image(
                model = wishItem.imageUrl,
                modifier = imageModifier,
                placeHolder = {
                    WishBoardInitialPlaceHolder(modifier = imageModifier)
                },
            )

            if (wishItem.itemOwnershipStatus == WishItemOwnershipStatus.OWNED) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .zIndex(2f)
                        .alpha(0.8f)
                        .background(color = WishBoardTheme.colors.green700)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "소장템",
                        style = WishBoardTheme.typography.suitB5,
                        color = WishBoardTheme.colors.white,
                    )
                }
            }

            /* 장바구니 버튼
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomEnd),
            ) {
                CartButton(
                    isInCart = cartState,
                    changeCartState = { isInCart -> cartState = !isInCart },
                )
            }*/

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .zIndex(3f)
                        .background(color = Color.Black.copy(alpha = 0.55f)),
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .zIndex(3f)
                        .padding(5.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.ic_item_check),
                    contentDescription = "선택됨",
                    tint = Color.Unspecified,
                )
            }
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

@Composable
fun WishItemForListView(
    wishItem: WishItem,
    isSelected: Boolean = false,
    onClickItem: () -> Unit = {},
) {
    val imageModifier = Modifier.fillMaxSize()

    Row(
        // 이미지/텍스트마다 별도로 클릭 영역을 두면 각 영역의 제스처 인식기가 서로 경쟁해
        // 드래그 선택(LazyColumn에 걸린 롱프레스 드래그)이 이미지 위에서만 먹히는 문제가 있었다.
        // Row 전체를 하나의 클릭 영역으로 통일해 터치/드래그 선택 영역을 행 전체로 넓힌다.
        modifier = Modifier
            .noRippleClickable { onClickItem() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(84.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxHeight()
                .aspectRatio(1f),
        ) {
            Image(
                modifier = imageModifier,
                model = wishItem.imageUrl,
                placeHolder = {
                    WishBoardInitialPlaceHolder(modifier = imageModifier)
                },
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(color = Color.Black.copy(alpha = 0.55f)),
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.ic_item_check),
                    contentDescription = "선택됨",
                    tint = Color.Unspecified,
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = wishItem.name,
                style = WishBoardTheme.typography.suitD2M,
                color = WishBoardTheme.colors.gray700,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .alpha(if (wishItem.itemOwnershipStatus == WishItemOwnershipStatus.WISH) 0f else 1f)
                        .background(
                            color = WishBoardTheme.colors.green700,
                            shape = RoundedCornerShape(2.dp),
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "소장템",
                        style = WishBoardTheme.typography.suitB5,
                        color = WishBoardTheme.colors.white,
                    )
                }

                PriceText(
                    modifier = Modifier,
                    price = wishItem.price ?: 0,
                    priceStyle = WishBoardTheme.typography.montserratH2,
                    wonStyle = WishBoardTheme.typography.suitD2,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff, widthDp = 187, heightDp = 257)
@Composable
fun PreviewWishItem() {
    WishItemForGridView(
        wishItem = WishItem(
            id = 1L,
            name = "21SS SAGE SHIRT [4COLOR]",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun PreviewWishItemForListView() {
    WishItemForListView(
        wishItem = WishItem(
            id = 1L,
            name = "21SS SAGE SHIRT [4COLOR]",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff, widthDp = 187, heightDp = 257)
@Composable
fun PreviewWishItemSelected() {
    WishItemForGridView(
        wishItem = WishItem(
            id = 1L,
            name = "21SS SAGE SHIRT [4COLOR]",
            imageUrl = "https://url.kr/8vwf1e",
            price = 108000,
        ),
        isSelected = true,
    )
}
