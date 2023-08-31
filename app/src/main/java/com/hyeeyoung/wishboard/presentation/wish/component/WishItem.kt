package com.hyeeyoung.wishboard.presentation.wish.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.button.CartButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.WishItem
import com.hyeeyoung.wishboard.presentation.util.extension.setPriceFormat

@Composable
fun WishItem(wishItem: WishItem) {
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
