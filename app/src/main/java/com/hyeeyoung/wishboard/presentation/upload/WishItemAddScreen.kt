package com.hyeeyoung.wishboard.presentation.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardNarrowButton
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardSimpleTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.model.WishItemDetail

@Composable
fun WishItemUploadScreen(navController: NavHostController, itemDetail: WishItemDetail? = null) {
    WishboardTheme {
        Scaffold(topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(
                    startIcon = WishBoardTopBarModel.TopBarIcon.CLOSE,
                    title = stringResource(
                        id = if (itemDetail != null) {
                            R.string.wish_item_upload_add_title
                        } else {
                            R.string.wish_item_upload_edit_title
                        },
                    ),
                    onClickStartIcon = { navController.popBackStack() },
                ),
                endComponent = { modifier ->
                    Row(modifier = modifier) {
                        WishBoardNarrowButton(
                            enabled = true,
                            onClick = { /*TODO*/ },
                            text = stringResource(id = R.string.save),
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                },
            )
        }) { paddingValues ->
            val nameInput = remember { mutableStateOf("") }
            val priceInput = remember { mutableStateOf("") }
            val memoInput = remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp)
                    .verticalScroll(
                        rememberScrollState(),
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val imageHeight = LocalConfiguration.current.screenWidthDp * 0.66
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight.dp)
                        .background(
                            color = WishBoardTheme.colors.gray100,
                            shape = RoundedCornerShape(32.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    WishBoardIconButton(iconRes = R.drawable.ic_camera, onClick = { /*TODO*/ })
                }
                WishBoardSimpleTextField(
                    input = nameInput,
                    placeholder = stringResource(id = R.string.wish_item_upload_item_name),
                    onTextChange = {},
                )
                WishBoardDivider()
                WishBoardSimpleTextField(
                    input = priceInput,
                    placeholder = stringResource(id = R.string.wish_item_upload_item_price),
                    onTextChange = {},
                )
                WishBoardDivider()
                ItemInfoRow(label = stringResource(id = R.string.folder))
                WishBoardDivider()
                ItemInfoRow(label = stringResource(id = R.string.wish_item_upload_noti))
                WishBoardDivider()
                ItemInfoRow(
                    label = stringResource(id = R.string.wish_item_upload_shop_link),
                    guideText = stringResource(
                        id = R.string.wish_item_upload_shop_link_guide,
                    ),
                )
                WishBoardDivider()
                WishBoardSimpleTextField(
                    input = memoInput,
                    placeholder = stringResource(id = R.string.wish_item_upload_memo),
                    onTextChange = {},
                )
                WishBoardDivider()
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Composable
fun ItemInfoRow(label: String, guideText: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = WishBoardTheme.typography.suitB3,
            color = WishBoardTheme.colors.gray700,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!guideText.isNullOrEmpty()) {
                Text(
                    text = guideText,
                    style = WishBoardTheme.typography.suitD3,
                    color = WishBoardTheme.colors.green700,
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_detail),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@Preview
@Composable
fun PreviewWishItemUploadScreen() {
    WishItemUploadScreen(rememberNavController())
}
