package com.hyeeyoung.wishboard.presentation.upload

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardMiniSingleTextField
import com.hyeeyoung.wishboard.designsystem.style.MontserratFamily
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.designsystem.util.PriceTransformation
import com.hyeeyoung.wishboard.presentation.model.FolderSummary
import com.hyeeyoung.wishboard.presentation.util.extension.isEmptyOrBlank
import com.hyeeyoung.wishboard.presentation.util.extension.makeValidPriceStr
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

private const val IMAGE_SIZE = 80

@Composable
fun LinkSharingWishUploadScreen(url: String, onClickClose: () -> Unit = {}) {
    val nameInput = remember { mutableStateOf("") }
    val priceInput = remember { mutableStateOf("") }
    val image = "https://url.kr/8vwf1e" // TODO 서버 연동 필요

    WishboardTheme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Box(contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = (IMAGE_SIZE / 2).dp)
                        .background(
                            color = WishBoardTheme.colors.white,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        )
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 5.dp, end = 8.dp),
                    ) {
                        WishBoardIconButton(
                            iconRes = R.drawable.ic_close,
                            onClick = { onClickClose() },
                        )
                    }

                    Spacer(modifier = Modifier.size(7.dp))

                    val textFieldModifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                    WishBoardMiniSingleTextField(
                        modifier = textFieldModifier,
                        input = nameInput,
                        placeholder = stringResource(id = R.string.wish_item_link_sharing_upload_name),
                        onTextChange = {},
                    )

                    WishBoardMiniSingleTextField(
                        modifier = textFieldModifier,
                        input = priceInput,
                        style = TextStyle(
                            fontFamily = MontserratFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        ),
                        placeholder = stringResource(id = R.string.wish_item_link_sharing_upload_price),
                        onTextChange = { input ->
                            priceInput.value = input.makeValidPriceStr() ?: ""
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PriceTransformation(),
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Row(
                        modifier = Modifier
                            .noRippleClickable { /*TODO*/ }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(id = R.drawable.ic_nav_notice),
                            contentDescription = null,
                            tint = WishBoardTheme.colors.gray700,
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = stringResource(id = R.string.wish_item_link_sharing_upload_noti_setting),
                            style = WishBoardTheme.typography.suitD3,
                            color = WishBoardTheme.colors.gray700,
                        )

                        // TODO 사용자 입력 알림 데이터 여부에 따른 visibility 조절
//                        Spacer(modifier = Modifier.size(2.dp))
//                        Icon(
//                            modifier = Modifier
//                                .padding(2.dp)
//                                .size(14.dp),
//                            painter = painterResource(id = R.drawable.ic_delete_circle),
//                            contentDescription = null,
//                            tint = Color.Unspecified
//                        )
                    }

                    val folders = listOf(
                        FolderSummary(1L, "아우터", "https://url.kr/8vwf1e"),
                        FolderSummary(2L, "상의", "https://url.kr/8vwf1e"),
                        FolderSummary(3L, "하ㅑ", "https://url.kr/8vwf1e"),
                        FolderSummary(4L, "악세사리", "https://url.kr/8vwf1e"),
                        FolderSummary(5L, "케이스", "https://url.kr/8vwf1e"),
                    )

                    var selectedFolder by remember { mutableStateOf(folders.first()) }

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item {
                            NewFolder()
                        }
                        items(folders) {
                            FolderItem(
                                isSelected = selectedFolder == it,
                                folder = it,
                                onClickFolder = { folder -> selectedFolder = folder },
                            ) // TODO 상태 관리
                        }
                        item {
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }

                    val isLogin = true // TODO 로컬 디비 연결
                    val buttonTextRes =
                        if (isLogin) {
                            R.string.wish_item_link_sharing_upload
                        } else {
                            R.string.wish_item_link_sharing_upload_after_login
                        }

                    WishBoardWideButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        enabled = isLogin &&
                            !nameInput.value.isEmptyOrBlank() &&
                            !priceInput.value.isEmptyOrBlank() &&
                            !image.isEmptyOrBlank(),
                        onClick = { /*TODO*/ },
                        text = stringResource(id = buttonTextRes),
                    )
                }

                val imageModifier = Modifier
                    .size(IMAGE_SIZE.dp)
                    .clip(CircleShape)
                if (!image.isNullOrEmpty()) {
                    ColoredImage(
                        modifier = imageModifier,
                        model = image,
                        contentDescription = null,
                    )
                } else {
                    Image(
                        modifier = imageModifier,
                        painter = painterResource(id = R.drawable.ic_black_logo),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun FolderItem(
    isSelected: Boolean,
    folder: FolderSummary,
    onClickFolder: (FolderSummary) -> Unit,
) {
    Box(
        modifier = Modifier
            .noRippleClickable { onClickFolder(folder) }
            .clip((RoundedCornerShape(10.dp))),
    ) {
        ColoredImage(
            model = folder.thumbnail,
            modifier = Modifier.size(IMAGE_SIZE.dp),
            alphaColor = if (isSelected) WishBoardTheme.colors.blackAlpha70 else WishBoardTheme.colors.blackAlpha30,
        )

        Column(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_white),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }

            Text(
                text = folder.name,
                style = WishBoardTheme.typography.suitH6,
                color = WishBoardTheme.colors.white,
            )
        }
    }
}

@Composable
fun NewFolder() {
    Column(
        modifier = Modifier
            .size(IMAGE_SIZE.dp)
            .noRippleClickable { /*TODO*/ }
            .border(width = 1.dp, color = WishBoardTheme.colors.gray100, shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus_circle_gray),
                contentDescription = null,
                tint = Color.Unspecified,
            )

            Text(
                text = stringResource(id = R.string.wish_item_link_sharing_upload_new_folder),
                style = WishBoardTheme.typography.suitH6,
                color = WishBoardTheme.colors.gray300,
            )
        }
    }
}

@Preview
@Composable
fun PreviewLinkSharingWishUploadScreen() {
    LinkSharingWishUploadScreen(url = "")
}
