package com.hyeeyoung.wishboard.presentation.upload.component

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.ModalTitle
import com.hyeeyoung.wishboard.designsystem.component.loading.ThreeDotsLoadingView
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.model.WishBoardButtonColors
import com.hyeeyoung.wishboard.presentation.util.annotation.DefaultPreview
import com.hyeeyoung.wishboard.presentation.util.extension.checkValidationItemUrl

@Composable
fun ShopLinkModalContent(
    link: String? = null,
    isLoadingItem: Boolean = false,
    onClickComplete: (String) -> Unit,
    onClickLoadItem: (String) -> Unit = {},
    onDismissRequest: () -> Unit,
) {
    val linkInput = remember { mutableStateOf(link ?: "") }
    val isValidUrl by remember(linkInput.value) {
        mutableStateOf(if (linkInput.value.isEmpty()) null else linkInput.value.checkValidationItemUrl())
    }
    // 아이템 정보 불러오기 로딩이 끝나면(성공/실패 모두) 바텀시트를 닫는다.
    var hasTriggeredLoad by remember { mutableStateOf(false) }

    LaunchedEffect(isLoadingItem) {
        if (hasTriggeredLoad && !isLoadingItem) {
            onDismissRequest()
        }
    }

    Column {
        ModalTitle(
            title = stringResource(id = R.string.modal_shop_link_title),
            onDismissRequest = onDismissRequest,
        )

        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            if (isLoadingItem) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(216.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    ThreeDotsLoadingView()
                }
            } else {
                Spacer(modifier = Modifier.height(76.dp))

                Box(contentAlignment = Alignment.Center) {
                    WishBoardTextField(
                        input = linkInput,
                        isError = isValidUrl == false,
                        errorHidingStrategy = View.INVISIBLE,
                        onTextChange = { linkInput.value = it.trim() },
                        placeholder = stringResource(id = R.string.modal_shop_link_placeholder),
                        errorMsg = stringResource(id = R.string.modal_shop_link_error),
                    )
                }

                Spacer(modifier = Modifier.height(58.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                    WishBoardButton(
                        modifier = Modifier.weight(1f),
                        enabled = isValidUrl == true,
                        color = WishBoardButtonColors.WHITE,
                        onClick = {
                            hasTriggeredLoad = true
                            onClickLoadItem(linkInput.value)
                        },
                        text = stringResource(id = R.string.modal_shop_link_item_load_btn_text),
                    )

                    WishBoardButton(
                        modifier = Modifier.weight(1f),
                        enabled = isValidUrl == true,
                        onClick = { onClickComplete(linkInput.value) },
                        text = "링크만 등록하기",
                    )
                }
            }
        }
    }
}

@DefaultPreview
@Composable
fun PreviewShopLinkModalContent() {
    ShopLinkModalContent(onClickComplete = {}, onDismissRequest = {})
}

@DefaultPreview
@Composable
fun PreviewShopLinkModalContentLoading() {
    ShopLinkModalContent(
        link = "https://www.musinsa.com/app/goods/2377269",
        isLoadingItem = true,
        onClickComplete = {},
        onDismissRequest = {},
    )
}
