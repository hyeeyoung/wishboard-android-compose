package com.hyeeyoung.wishboard.presentation.upload.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.ModalTitle
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.presentation.util.extension.checkValidationItemUrl

@Composable
fun ShopLinkModalContent(link: String? = null, onClickComplete: (String) -> Unit, onDismissRequest: () -> Unit) {
    val linkInput = remember { mutableStateOf(link ?: "") }
    val isValidUrl by remember(linkInput.value) {
        mutableStateOf(if (linkInput.value.isEmpty()) null else linkInput.value.checkValidationItemUrl())
    }

    Column {
        ModalTitle(
            title = stringResource(id = R.string.modal_shop_link_title),
            onDismissRequest = onDismissRequest
        )

        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            Spacer(modifier = Modifier.height(76.dp))

            Box(contentAlignment = Alignment.Center) {
                WishBoardTextField(
                    input = linkInput,
                    isError = isValidUrl == false,
                    onTextChange = { linkInput.value = it.trim() },
                    placeholder = stringResource(id = R.string.modal_shop_link_placeholder),
                    errorMsg = stringResource(id = R.string.modal_shop_link_error),
                )
            }

            Spacer(modifier = Modifier.height(58.dp))

            WishBoardWideButton(
                enabled = isValidUrl == true,
                onClick = { onClickComplete(linkInput.value) },
                text = stringResource(id = R.string.complete),
            )
        }
    }
}

@Composable
@Preview
fun PreviewShopLinkModalContent() {
    ShopLinkModalContent(onClickComplete = {}, onDismissRequest = {})
}
