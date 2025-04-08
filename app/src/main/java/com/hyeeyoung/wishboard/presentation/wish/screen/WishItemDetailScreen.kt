package com.hyeeyoung.wishboard.presentation.wish.screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen.Upload.ARG_ITEM_DETAIL
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardTwoButtonDialog
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.image.WishBoardPlaceHolder
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.Gray700
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardString
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.buildStringWithSpans
import com.hyeeyoung.wishboard.presentation.util.extension.formatAsTimeAgo
import com.hyeeyoung.wishboard.presentation.util.extension.formatDday
import com.hyeeyoung.wishboard.presentation.util.extension.getAndRemove
import com.hyeeyoung.wishboard.presentation.util.extension.getDomainName
import com.hyeeyoung.wishboard.presentation.util.extension.moveToWebView
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.util.extension.toBase64Json
import com.hyeeyoung.wishboard.presentation.util.safeLet
import com.hyeeyoung.wishboard.presentation.wish.component.PriceText
import com.hyeeyoung.wishboard.presentation.wish.model.WishItemDetailUiModel
import kotlinx.datetime.LocalDateTime

const val IS_WISH_ITEM_MODIFIED = "isWishItemModified"

@Composable
fun WishItemDetailScreen(
    navController: NavController,
    itemId: Long,
    viewModel: WishItemViewModel = hiltViewModel()
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    LaunchedEffect(Unit) {
        viewModel.getWishItemDetail(itemId)
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle
            .getAndRemove<Boolean>(IS_WISH_ITEM_MODIFIED) { agreedTermType ->
               // TODO 확인 필요
            }
    }

    WishItemDetailScreen(
        uiModel = uiModel,
        updateFolder = {
            viewModel.updateFolder(it)
        },
        onClickEdit = {
            navController.navigate(
                "${MainScreen.Upload.route}?$ARG_ITEM_DETAIL=${
                    uiModel.toBase64Json()
                }",
            )
        },
        onClickShop = {
            if (!uiModel.site.isNullOrEmpty()) {
                navController.moveToWebView(
                    title = uiModel.site!!.getDomainName(),
                    url = uiModel.site!!,
                )
            }
        },
        onClickDelete = { itemId ->
            viewModel.deleteWishItem(itemId = itemId) {
                navController.safePopBackStack()
            }
        },
        onClickBack = navController::safePopBackStack,
        onClickFolder = { afterSuccess ->
            viewModel.getFolders(afterSuccess)
        }
    )
}

@Composable
fun WishItemDetailScreen(
    uiModel: WishItemDetailUiModel,
    updateFolder: (FolderItem) -> Unit,
    onClickFolder: ((List<FolderItem>) -> Unit) -> Unit,
    onClickEdit: () -> Unit,
    onClickShop: () -> Unit,
    onClickBack: () -> Unit,
    onClickDelete: (itemId: Long?) -> Unit,
) {
    val context = LocalContext.current
    val modalLauncher = rememberModalLauncher { _, data ->
        when (data) {
            is ModalData.Modal.FolderList -> {
                data.selectedFolder?.let(updateFolder)
            }

            else -> {}
        }
    }
    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setNavigationBarColor(color = Gray700)
    }

    Scaffold(topBar = {
        WishBoardTopBar(
            WishBoardTopBarModel(onClickStartIcon = onClickBack),
            endComponent = { modifier ->
                TopBarEndIcons(
                    modifier,
                    onClickDelete = { dialogData = DialogData.WishItemDelete(uiModel.id) },
                    onClickEdit = onClickEdit,
                )
            },
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            WishItemDetailContents(
                modifier = Modifier.weight(1f),
                uiModel = uiModel,
                onClickFolder = {
                    onClickFolder { folders ->
                        ModalData.Modal.FolderList(
                            selectedFolder = uiModel.folderId?.let { FolderItem(id = it) },
                            folders = folders
                        )
                            .openModal(context, modalLauncher)
                    }
                },
            )

            WishBoardWideButton(
                enabled = uiModel.site != null,
                onClick = {
                    onClickShop()
                },
                text = stringResource(id = R.string.wish_item_detail_go_to_shop),
                shape = RectangleShape,
                isGreen = false,
            )
        }

        WishBoardTwoButtonDialog(
            dialogData = dialogData,
            onClickConfirm = {
                when (dialogData) {
                    is DialogData.WishItemDelete -> {
                        val itemId = (dialogData as DialogData.WishItemDelete).itemId
                        onClickDelete(itemId)
                    }

                    else -> {}
                }
            },
            onDismissRequest = { dialogData = null },
        )
    }
}

@Composable
private fun WishItemDetailContents(modifier: Modifier, uiModel: WishItemDetailUiModel, onClickFolder: () -> Unit) {
    val imageModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(32.dp))
        .aspectRatio(1f / 1.15f)

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            Image(
                modifier = imageModifier,
                model = uiModel.image,
                placeHolder = {
                    WishBoardPlaceHolder(modifier = imageModifier)
                }
            )

            safeLet(uiModel.notiType, uiModel.notiDate) { type, date ->
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
            FolderGuideString(
                folderName = uiModel.folderName,
                onClickFolder = onClickFolder,
            )
            Text(
                text = uiModel.createAt?.formatAsTimeAgo() ?: "",
                style = WishBoardTheme.typography.suitD3,
                color = WishBoardTheme.colors.gray300,
            )
        }

        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 20.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = uiModel.name,
                style = WishBoardTheme.typography.suitB1M,
                color = WishBoardTheme.colors.gray700,
            )

            PriceText(
                modifier = Modifier.padding(top = 20.dp),
                price = uiModel.price,
                priceStyle = WishBoardTheme.typography.montserratH2,
                wonStyle = WishBoardTheme.typography.suitD2,
            )
        }

        uiModel.site?.getDomainName()?.let { site ->
            WishBoardDivider()
            Text(
                modifier = Modifier.padding(16.dp),
                text = site,
                style = WishBoardTheme.typography.suitD3,
                color = WishBoardTheme.colors.gray300,
            )
        }

        uiModel.memo?.let { memo ->
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
private fun TopBarEndIcons(modifier: Modifier, onClickDelete: () -> Unit, onClickEdit: () -> Unit) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        WishBoardIconButton(iconRes = R.drawable.ic_trash, onClick = { onClickDelete() })
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
            text = type.label,
            style = WishBoardTheme.typography.suitB5,
            color = WishBoardTheme.colors.gray700,
        )
        Text(
            modifier = labelModifier,
            text = date.formatDday(),
            style = WishBoardTheme.typography.suitB5,
            color = WishBoardTheme.colors.gray700,
        )
    }
}

@Composable
private fun FolderGuideString(folderName: String?, onClickFolder: () -> Unit) {
    val folderNameOrGuide =
        if (folderName.isNullOrEmpty()) {
            stringResource(id = R.string.wish_item_detail_folder_guild)
        } else {
            folderName
        }
    val spanStrings = listOf(
        WishBoardString.SpanString(value = folderNameOrGuide),
        WishBoardString.NormalString(value = " >"),
    )

    Text(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .noRippleClickable { onClickFolder() },
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
    WishItemDetailScreen(
        uiModel = WishItemDetailUiModel(
            id = 1L,
            name = "21SS SAGE SHIRT [4COLOR]",
            image = "https://url.kr/8vwf1e",
            price = 108000,
            notiDate = LocalDateTime(2024, 1, 13, 1, 13),
            notiType = NotiType.RESTOCK,
            site = "https://www.naver.com/",
            memo = "S사이즈",
            folderId = 1L,
            folderName = "상의",
            createAt = LocalDateTime(2025, 3, 20, 2, 0),
        ),
        updateFolder = {},
        onClickShop = {},
        onClickEdit = {},
        onClickDelete = {},
        onClickBack = {},
        onClickFolder = {},
    )
}
