package com.hyeeyoung.wishboard.presentation.noti

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.Calendar
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.LegacyWishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.Green500
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.formatAsTimeAgo
import com.hyeeyoung.wishboard.presentation.util.extension.getDomainName
import com.hyeeyoung.wishboard.presentation.util.extension.moveToWebView
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotiScreen(
    navController: NavHostController,
    viewModel: NotiViewModel = hiltViewModel(),
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    var isFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isFetched) {
        if (!isFetched) {
            viewModel.fetchPreviousNoti(false)
            isFetched = true
        }
    }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    PullToRefreshBox(
        isRefreshing = uiModel.isRefreshing,
        onRefresh = {
            viewModel.fetchPreviousNoti(true)
        },
    ) {
        NotiScreen(
            notiList = uiModel.notiList,
            updateReadState = viewModel::updateReadState,
            updateSnackbarMessage = viewModel::updateSnackbarMessage,
            moveToWebView = { title, url ->
                navController.moveToWebView(
                    title = title,
                    url = url,
                )
            },
            onClickCalendar = {
                navController.navigate(Calendar.route)
            },
            onClickBack = navController::safePopBackStack,
        )
    }
}

@Composable
fun NotiScreen(
    notiList: List<NotiItem>,
    updateReadState: (id: Long) -> Unit,
    updateSnackbarMessage: (String) -> Unit,
    moveToWebView: (title: String?, url: String) -> Unit,
    onClickCalendar: () -> Unit,
    onClickBack: () -> Unit,
) {
    val snackbarMsgForNotiLink = stringResource(id = R.string.noti_item_url_snackbar_text)

    Scaffold(
        topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(
                    title = stringResource(id = R.string.noti),
                    onClickStartIcon = onClickBack,
                ),
                endComponent = {
                    Row(modifier = it) {
                        LegacyWishBoardIconButton(iconRes = R.drawable.ic_calendar, onClick = { onClickCalendar() })
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                },
            )
        },
    ) { paddingValues ->
        val contentModifier = Modifier
            .fillMaxSize()
            .background(WishBoardTheme.colors.white)
            .padding(top = paddingValues.calculateTopPadding())

        if (notiList.isEmpty()) {
            WishBoardEmptyView(modifier = contentModifier, guideTextRes = R.string.empty_noti_guide_text)
        } else {
            LazyColumn(modifier = contentModifier.padding(top = 7.dp)) {
                itemsIndexed(notiList) { idx, noti ->
                    NotiItem(
                        noti = noti,
                        onClickNotiWithLink = { site ->
                            moveToWebView(site.getDomainName(), site)
                            updateReadState(noti.itemId)
                        },
                        onClickNotiWithoutLink = {
                            updateSnackbarMessage(snackbarMsgForNotiLink)
                            updateReadState(noti.itemId)
                        },
                    )
                    if (idx < notiList.lastIndex) WishBoardDivider()
                }
            }
        }
    }
}

@Composable
fun NotiItem(noti: NotiItem, onClickNotiWithLink: (String) -> Unit = {}, onClickNotiWithoutLink: () -> Unit = {}) {
    val imageSize = 80
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .noRippleClickable {
                if (!noti.itemUrl.isNullOrEmpty()) {
                    onClickNotiWithLink(noti.itemUrl)
                } else {
                    onClickNotiWithoutLink()
                }
            },
    ) {
        Image(
            model = noti.itemImage,
            modifier = Modifier
                .size(imageSize.dp)
                .clip(CircleShape),
        )
        Column(
            modifier = Modifier
                .height(imageSize.dp)
                .padding(start = 10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "${noti.notiType.label} ${stringResource(id = R.string.noti)}",
                    style = WishBoardTheme.typography.suitH5,
                    color = WishBoardTheme.colors.gray700,
                )
                if (!noti.isRead) Canvas(modifier = Modifier.size(8.dp), onDraw = { drawCircle(color = Green500) })
            }

            Text(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .weight(1f),
                text = noti.itemName,
                style = WishBoardTheme.typography.suitD3M,
                color = WishBoardTheme.colors.gray700,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = noti.notiDate?.formatAsTimeAgo() ?: "",
                style = WishBoardTheme.typography.suitD3,
                color = WishBoardTheme.colors.gray200,
            )
        }
    }
}

@Composable
@Preview
fun PreviewNotiScreen() {
    val notiItem = NotiItem(
        itemId = 1L,
        itemName = "Bean Ring Gold",
        itemImage = "https://url.kr/8vwf1e",
        notiType = NotiType.RESTOCK,
        notiDate = kotlinx.datetime.LocalDateTime(2025, 3, 20, 13, 13),
        isRead = false,
        itemUrl = "https://www.naver.com/",
    )

    val notiList = List(7) { idx -> notiItem.copy(itemId = idx.toLong()) }

    NotiScreen(notiList = notiList, updateReadState = {
    }, updateSnackbarMessage = {}, moveToWebView = { _, _ -> }, onClickCalendar = {}, onClickBack = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewNotiItem() {
    val notiItem = NotiItem(
        itemId = 1L,
        itemName = "Bean Ring Gold",
        itemImage = "https://url.kr/8vwf1e",
        notiType = NotiType.RESTOCK,
        notiDate = kotlinx.datetime.LocalDateTime(2024, 3, 20, 13, 13),
        isRead = false,
        itemUrl = "https://www.naver.com/",
    )

    Column {
        NotiItem(
            noti = notiItem,
        )
    }
}
