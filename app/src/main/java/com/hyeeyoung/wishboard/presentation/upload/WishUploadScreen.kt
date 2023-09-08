package com.hyeeyoung.wishboard.presentation.upload

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardNarrowButton
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardSimpleTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.designsystem.util.PriceTransformation
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.util.extension.getCurrentTime
import com.hyeeyoung.wishboard.presentation.util.extension.makeValidPriceStr
import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import kotlinx.datetime.LocalDateTime

@Composable
fun WishUploadScreen(navController: NavHostController, itemDetail: WishItemDetail? = null) {
    val imageInput by remember { mutableStateOf<Uri?>(null) }
    val nameInput = remember { mutableStateOf(itemDetail?.name ?: "") }
    val priceInput = remember { mutableStateOf(itemDetail?.price?.toString() ?: "") }
    val memoInput = remember { mutableStateOf(itemDetail?.memo ?: "") }

    WishboardTheme {
        Scaffold(topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(
                    startIcon = WishBoardTopBarModel.TopBarIcon.CLOSE,
                    title = stringResource(
                        id = if (itemDetail == null) {
                            R.string.wish_item_upload_add_title
                        } else {
                            R.string.wish_item_upload_edit_title
                        },
                    ),
                    onClickStartIcon = { navController.popBackStack() },
                ),
                endComponent = { modifier ->
                    Row(modifier = modifier) {
                        // TODO 뷰모델로 버튼 활성화 로직 옮기기
                        WishBoardNarrowButton(
                            enabled = nameInput.value.isNotEmpty() &&
                                nameInput.value.isNotBlank() &&
                                priceInput.value.isNotEmpty() &&
                                (imageInput != null || !itemDetail?.image.isNullOrEmpty()),
                            onClick = { /*TODO*/ },
                            text = stringResource(id = R.string.save),
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                },
            )
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WishBoardTheme.colors.white)
                    .padding(top = 6.dp + paddingValues.calculateTopPadding(), bottom = 16.dp)
                    .verticalScroll(rememberScrollState()),
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
                    AsyncImage(
                        modifier = Modifier.fillMaxHeight(),
                        model = imageInput ?: itemDetail?.image,
                        contentDescription = null,
                    )

                    WishBoardIconButton(iconRes = R.drawable.ic_camera, onClick = { /*TODO*/ })
                }

                WishBoardSimpleTextField(
                    input = nameInput,
                    placeholder = stringResource(id = R.string.wish_item_upload_item_name),
                    onTextChange = {},
                )
                WishBoardSimpleTextField(
                    input = priceInput,
                    placeholder = stringResource(id = R.string.wish_item_upload_item_price),
                    onTextChange = { input ->
                        priceInput.value = input.makeValidPriceStr() ?: ""
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PriceTransformation(prefix = "₩ "),
                )
                ItemInfoRow(label = itemDetail?.folderName ?: stringResource(id = R.string.folder))
                ItemInfoRow(
                    label = getNotiInfo(notiType = itemDetail?.notiType, notiDate = itemDetail?.notiDate)
                        ?: stringResource(id = R.string.wish_item_upload_noti),
                )
                ItemInfoRow(
                    label = stringResource(id = R.string.wish_item_upload_shop_link),
                    guideText = stringResource(
                        id = R.string.wish_item_upload_shop_link_guide,
                    ),
                )
                WishBoardSimpleTextField(
                    input = memoInput,
                    placeholder = stringResource(id = R.string.wish_item_upload_memo),
                    onTextChange = {},
                )
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Composable
fun ItemInfoRow(label: String, guideText: String? = null) {
    Column {
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
        WishBoardDivider()
    }
}

@Composable
fun getNotiInfo(notiType: NotiType?, notiDate: LocalDateTime?): String? =
    if (notiType == null || notiDate == null) {
        null
    } else {
        "[${stringResource(id = R.string.noti_item_type, formatArgs = arrayOf(notiType.str))}] $notiDate"
    }

@Preview
@Composable
fun PreviewWishUploadScreen() {
    WishUploadScreen(
        navController = rememberNavController(),
        itemDetail = WishItemDetail(
            id = 1L,
            name = "21SS SAGE SHIRT [4COLOR]",
            image = "https://url.kr/8vwf1e",
            price = 108000,
            notiDate = getCurrentTime(),
            notiType = NotiType.RESTOCK,
            site = "https://www.naver.com/",
            memo = "S사이즈",
            folderId = 1L,
            folderName = "상의",
            createAt = "1주 전",
        ),
    )
}