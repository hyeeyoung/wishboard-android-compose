package com.hyeeyoung.wishboard.presentation.wish

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen.Upload.ARG_ITEM_DETAIL
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.Gray700
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardString
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.util.buildStringWithSpans
import com.hyeeyoung.wishboard.presentation.util.extension.getCurrentTime
import com.hyeeyoung.wishboard.presentation.util.extension.getDomainName
import com.hyeeyoung.wishboard.presentation.util.extension.moveToWebView
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.safeLet
import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import com.hyeeyoung.wishboard.presentation.wish.component.PriceText
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun WishItemDetailScreen(navController: NavHostController, itemId: Long) {
    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit) {
        systemUiController.setNavigationBarColor(color = Gray700)
        onDispose { systemUiController.setNavigationBarColor(color = Color.Transparent) }
    }

    val itemDetail = WishItemDetail(
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
    ) // TODO 시간 포맷 적용 및 서버 연동 시 삭제

    WishboardTheme { // TODO Theme 사용 여부 고려
        Scaffold(topBar = {
            WishBoardTopBar(
                WishBoardTopBarModel(onClickStartIcon = { navController.popBackStack() }),
                endComponent = { modifier ->
                    TopBarEndIcons(
                        modifier,
                        onClickEdit = {
                            navController.navigate(
                                "${MainScreen.Upload.route}?$ARG_ITEM_DETAIL=${
                                    Json.encodeToString(itemDetail)
                                }",
                            )
                        },
                    )
                },
            )
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding()),
            ) {
                WishItemDetailContents(modifier = Modifier.weight(1f), itemDetail = itemDetail)

                WishBoardWideButton(
                    enabled = itemDetail.site != null,
                    onClick = {
                        if (!itemDetail.site.isNullOrEmpty()) {
                            navController.moveToWebView(title = itemDetail.site.getDomainName(), url = itemDetail.site)
                        }
                    },
                    text = stringResource(id = R.string.wish_item_detail_go_to_shop),
                    shape = RectangleShape,
                    isGreen = false,
                ) // TODO 비활성화 처리
            }
        }
    }
}

@Composable
private fun WishItemDetailContents(modifier: Modifier, itemDetail: WishItemDetail) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            ColoredImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .aspectRatio(1f / 1.15f),
                model = itemDetail.image,
            )

            safeLet(itemDetail.notiType, itemDetail.notiDate) { type, date ->
                NotiInfoLabel(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    type = type,
                    date = date,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FolderGuideString()
            Text(
                text = itemDetail.createAt,
                style = WishBoardTheme.typography.suitD3,
                color = WishBoardTheme.colors.gray300,
            )
        }

        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 20.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = itemDetail.name,
                style = WishBoardTheme.typography.suitB1M,
                color = WishBoardTheme.colors.gray700,
            )

            PriceText(
                modifier = Modifier.padding(top = 20.dp),
                price = itemDetail.price,
                priceStyle = WishBoardTheme.typography.montserratH2,
                wonStyle = WishBoardTheme.typography.suitD2,
            )
        }

        itemDetail.site?.getDomainName()?.let { site ->
            WishBoardDivider()
            Text(
                modifier = Modifier.padding(16.dp),
                text = site,
                style = WishBoardTheme.typography.suitD3,
                color = WishBoardTheme.colors.gray300,
            )
        }

        itemDetail.memo?.let { memo ->
            WishBoardDivider()
            Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 64.dp)) {
                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = stringResource(id = R.string.memo),
                    style = WishBoardTheme.typography.suitB2,
                    color = WishBoardTheme.colors.gray700,
                )
                Text(
                    text = memo,
                    style = WishBoardTheme.typography.suitD2,
                    color = WishBoardTheme.colors.gray700,
                )
            }
        }
    }
}

@Composable
private fun TopBarEndIcons(modifier: Modifier, onClickEdit: () -> Unit) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        WishBoardIconButton(iconRes = R.drawable.ic_trash, onClick = { /*TODO*/ })
        WishBoardIconButton(iconRes = R.drawable.ic_edit, onClick = { onClickEdit() })
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Composable
private fun NotiInfoLabel(modifier: Modifier, type: NotiType, date: LocalDateTime) {
    val labelModifier = Modifier
        .background(
            color = WishBoardTheme.colors.green500,
            shape = RoundedCornerShape(16.dp),
        )
        .padding(vertical = 4.dp, horizontal = 8.dp)

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            modifier = labelModifier,
            text = type.str,
            style = WishBoardTheme.typography.suitB5,
            color = WishBoardTheme.colors.gray700,
        )
        Text(
            modifier = labelModifier,
            text = date.toString(), // TODO 시간 포맷 적용
            style = WishBoardTheme.typography.suitB5,
            color = WishBoardTheme.colors.gray700,
        )
    }
}

@Composable
private fun FolderGuideString() {
    val spanStrings = listOf(
        WishBoardString.SpanString(value = stringResource(id = R.string.wish_item_detail_folder_guild)),
        WishBoardString.NormalString(value = " >"),
    )

    Text(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .noRippleClickable { /*TODO*/ },
        text = buildStringWithSpans(
            spanStrings = spanStrings,
            spanStyle = SpanStyle(textDecoration = TextDecoration.Underline),
        ),
        style = WishBoardTheme.typography.suitD3,
        color = WishBoardTheme.colors.gray300,
    )
}

@Preview
@Composable
fun PreviewWishItemDetailScreen() {
    WishItemDetailScreen(navController = rememberNavController(), itemId = 1L)
}
