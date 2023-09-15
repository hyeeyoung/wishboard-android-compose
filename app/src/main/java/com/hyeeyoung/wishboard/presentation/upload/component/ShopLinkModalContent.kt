package com.hyeeyoung.wishboard.presentation.upload.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField

@Composable
fun ShopLinkModalContent(link: String? = null, onClickBtn: () -> Unit = {}) {
    val linkInput = remember { mutableStateOf(link ?: "") }
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp)) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            WishBoardTextField(
                input = linkInput,
                placeholder = stringResource(id = R.string.modal_shop_link_placeholder),
                errorMsg = stringResource(id = R.string.modal_shop_link_error),
            )
        }

        WishBoardWideButton(
            enabled = true,
            onClick = { onClickBtn() },
            text = stringResource(id = R.string.modal_shop_link_item_load_btn_text),
        )
    }
}

@Composable
@Preview
fun PreviewShopLinkModalContent() {
    ShopLinkModalContent()
}
