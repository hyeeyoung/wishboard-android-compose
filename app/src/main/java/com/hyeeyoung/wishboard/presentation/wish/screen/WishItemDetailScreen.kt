package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen.ImageDetail
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen.Upload.ARG_ITEM_DETAIL
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardTwoButtonDialog
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.image.WishBoardFullPlaceHolder
import com.hyeeyoung.wishboard.designsystem.component.loading.ThreeDotsLoadingView
import com.hyeeyoung.wishboard.designsystem.component.text.HyperlinkText
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.model.WishBoardButtonColors
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.presentation.folder.FolderListModalContent
import com.hyeeyoung.wishboard.presentation.onboarding.WishBoardIndicator
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardString
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import com.hyeeyoung.wishboard.presentation.util.buildStringWithSpans
import com.hyeeyoung.wishboard.presentation.util.extension.formatAsTimeAgo
import com.hyeeyoung.wishboard.presentation.util.extension.formatDday
import com.hyeeyoung.wishboard.presentation.util.extension.getDomainName
import com.hyeeyoung.wishboard.presentation.util.extension.moveToWebView
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.util.extension.toBase64Json
import com.hyeeyoung.wishboard.presentation.util.safeLet
import com.hyeeyoung.wishboard.presentation.wish.component.PriceText
import com.hyeeyoung.wishboard.presentation.wish.model.WishItemDetailUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

@Composable
fun WishItemDetailScreen(
    navController: NavController,
    itemId: Long,
    viewModel: WishItemViewModel = hiltViewModel(),
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val enabledShopButton by remember(uiModel.site) {
        mutableStateOf(uiModel.site != null)
    }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    WishItemDetailScreen(
        uiModel = uiModel,
        navController = navController,
        enabledShopButton = enabledShopButton,
        onClickToggleMemoEdit = viewModel::startEditingMemo,
        onMemoInputChanged = viewModel::onMemoInputChanged,
        onClickSaveMemo = viewModel::saveMemo,
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
        onClickDelete = { id ->
            viewModel.deleteWishItem(itemId = id) {
                WishBoardEventBus.notifyWishItemChanged()
                navController.safePopBackStack()
            }
        },
        onClickImage = { index ->
            navController.navigate(
                "${ImageDetail.route}?${ImageDetail.ARG_IMAGES}=${
                    uiModel.images.toBase64Json()
                }&${ImageDetail.ARG_INITIAL_INDEX}=$index",
            )
        },
        onClickBack = navController::safePopBackStack,
        onClickFolder = { afterSuccess ->
            viewModel.getFolders(afterSuccess)
        },
        updateItemOwnership = viewModel::updateItemOwnership,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishItemDetailScreen(
    uiModel: WishItemDetailUiModel,
    navController: NavController,
    enabledShopButton: Boolean,
    onClickToggleMemoEdit: () -> Unit,
    onMemoInputChanged: (String) -> Unit,
    onClickSaveMemo: () -> Unit,
    updateFolder: (FolderItem) -> Unit,
    onClickFolder: ((List<FolderItem>) -> Unit) -> Unit,
    onClickEdit: () -> Unit,
    onClickShop: () -> Unit,
    onClickBack: () -> Unit,
    onClickImage: (index: Int) -> Unit,
    updateItemOwnership: () -> Unit,
    onClickDelete: (itemId: Long?) -> Unit,
) {
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    var modalData by remember { mutableStateOf<ModalData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = {
        true
    })
    val coroutineScope = rememberCoroutineScope()

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
                .fillMaxSize()
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            // 최초 조회 로딩만 전체 화면 로딩뷰로 보여준다(메모 저장 후의 백그라운드 재조회 등은 제외).
            if (uiModel.fetchState is WishBoardState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ThreeDotsLoadingView()
                }
            } else {
                WishItemDetailContents(
                    modifier = Modifier.weight(1f),
                    uiModel = uiModel,
                    navController = navController,
                    onClickImage = onClickImage,
                    onClickToggleMemoEdit = onClickToggleMemoEdit,
                    onMemoInputChanged = onMemoInputChanged,
                    onClickSaveMemo = onClickSaveMemo,
                    onClickFolder = {
                        onClickFolder { folders ->
                            modalData = ModalData.Modal.FolderList(
                                selectedFolder = uiModel.folderId?.let { FolderItem(id = it) },
                                folders = folders,
                            )
                            coroutineScope.launch {
                                sheetState.show()
                            }
                        }
                    },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 30.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    WishBoardButton(
                        modifier = Modifier.weight(1f),
                        enabled = true,
                        onClick = {
                            updateItemOwnership()
                        },
                        text = if (!uiModel.isOwnedItem) "소장템으로 바꾸기" else "소장템에서 제거",
                        color = if (!uiModel.isOwnedItem) WishBoardButtonColors.WHITE else WishBoardButtonColors.GRAY,
                    )

                    if (enabledShopButton) {
                        WishBoardButton(
                            modifier = Modifier.weight(1f),
                            enabled = true,
                            onClick = {
                                onClickShop()
                            },
                            text = stringResource(id = R.string.wish_item_detail_go_to_shop),
                            color = WishBoardButtonColors.BLACK,
                        )
                    }
                }
            }
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

        WishBoardModal(
            isOpen = modalData != null,
            sheetState = sheetState,
            onDismissRequest = {
                modalData = null
            },
            content = {
                when (modalData) {
                    is ModalData.Modal.FolderList -> {
                        val folderData = (modalData as ModalData.Modal.FolderList)
                        FolderListModalContent(
                            selectedFolder = folderData.selectedFolder,
                            folders = folderData.folders,
                            onClickFolder = { folder ->
                                folder.let(updateFolder)
                                coroutineScope.launch { sheetState.hide() }
                                modalData = null
                            },
                            onDismissRequest = {
                                coroutineScope.launch { sheetState.hide() }
                                modalData = null
                            },
                        )
                    }

                    else -> {}
                }
            },
        )
    }
}

@Composable
private fun WishItemDetailContents(
    modifier: Modifier,
    navController: NavController,
    uiModel: WishItemDetailUiModel,
    onClickFolder: () -> Unit,
    onClickImage: (index: Int) -> Unit,
    onClickToggleMemoEdit: () -> Unit,
    onMemoInputChanged: (String) -> Unit,
    onClickSaveMemo: () -> Unit,
) {
    val imageModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f / 1.15f)
    val pagerState = rememberPagerState(pageCount = { uiModel.images.size })
    val imageShape = RoundedCornerShape(32.dp)

    Column(
        // imePadding()이 verticalScroll보다 먼저 적용되어야 키보드가 뜰 때 스크롤 가능 영역(뷰포트) 자체가
        // 줄어들어 포커스된 필드가 올바르게 보인다. 순서가 바뀌면 스크롤 가능한 콘텐츠만 늘어날 뿐
        // 뷰포트 크기는 그대로라 키보드에 가려진 영역이 스크롤로 드러나지 않는다.
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            if (uiModel.images.isNotEmpty()) {
                HorizontalPager(
                    modifier = Modifier.clip(imageShape),
                    state = pagerState,
                    beyondViewportPageCount = 3,
                ) { index ->
                    Image(
                        modifier = imageModifier.noRippleClickable { onClickImage(index) },
                        model = uiModel.images[index],
                        placeHolder = {
                            WishBoardFullPlaceHolder(modifier = imageModifier)
                        },
                    )
                }
            } else {
                WishBoardFullPlaceHolder(
                    modifier = imageModifier.background(
                        color = WishBoardTheme.colors.black.copy(alpha = 0.05f),
                        shape = imageShape,
                    ),
                )
            }

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

        if (uiModel.images.size > 1) {
            WishBoardIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp, bottom = 12.dp),
                size = uiModel.images.size,
                pagerState = pagerState,
            )
        } else {
            Spacer(modifier = Modifier.padding(top = 12.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
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

        WishBoardDivider()

        MemoSection(
            navController = navController,
            memo = uiModel.memo,
            isEditing = uiModel.isEditingMemo,
            memoInput = uiModel.memoInput,
            onClickToggleEdit = onClickToggleMemoEdit,
            onMemoInputChanged = onMemoInputChanged,
            onClickSave = onClickSaveMemo,
        )

        Spacer(modifier = Modifier.padding(bottom = 64.dp))
    }
}

@Composable
private fun MemoSection(
    navController: NavController,
    memo: String?,
    isEditing: Boolean,
    memoInput: String,
    onClickToggleEdit: () -> Unit,
    onMemoInputChanged: (String) -> Unit,
    onClickSave: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue(memoInput)) }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            // 편집 모드에 진입할 때 커서가 메모 맨 끝을 가리키도록 한다.
            textFieldValue = TextFieldValue(text = memoInput, selection = TextRange(memoInput.length))
            focusRequester.requestFocus()
            keyboardController?.show()
            // 키보드가 올라오는 애니메이션이 끝난 뒤 스크롤해야 필드가 정확히 보인다.
            delay(300L)
            bringIntoViewRequester.bringIntoView()
        }
    }

    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.memo),
                style = WishBoardTheme.typography.suitB2,
                color = WishBoardTheme.colors.gray700,
            )

            MemoActionButton(
                text = when {
                    isEditing -> "저장"
                    memo.isNullOrEmpty() -> "작성"
                    else -> "편집"
                },
                isPrimary = isEditing,
                onClick = if (isEditing) onClickSave else onClickToggleEdit,
            )
        }

        when {
            isEditing -> {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                        onMemoInputChanged(it.text)
                    },
                    textStyle = WishBoardTheme.typography.suitD2.copy(color = WishBoardTheme.colors.gray700),
                    cursorBrush = SolidColor(WishBoardTheme.colors.gray700),
                    decorationBox = { innerTextField ->
                        Box {
                            if (memoInput.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.wish_item_upload_memo),
                                    style = WishBoardTheme.typography.suitD2,
                                    color = WishBoardTheme.colors.gray200,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }

            memo.isNullOrEmpty() -> {
                Text(
                    text = stringResource(id = R.string.wish_item_upload_memo),
                    style = WishBoardTheme.typography.suitD2,
                    color = WishBoardTheme.colors.gray200,
                )
            }

            else -> {
                HyperlinkText(
                    navController = navController,
                    text = memo,
                    style = WishBoardTheme.typography.suitD2,
                    color = WishBoardTheme.colors.gray700,
                )
            }
        }
    }
}

@Composable
private fun MemoActionButton(text: String, isPrimary: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPrimary) WishBoardTheme.colors.gray700 else WishBoardTheme.colors.gray100)
            .noRippleClickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = WishBoardTheme.typography.suitB3,
            color = if (isPrimary) WishBoardTheme.colors.white else WishBoardTheme.colors.gray600,
        )
    }
}

@Composable
private fun TopBarEndIcons(modifier: Modifier, onClickDelete: () -> Unit, onClickEdit: () -> Unit) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        WishBoardIconButton(iconRes = R.drawable.ic_trash, onClick = { onClickDelete() })
        WishBoardIconButton(iconRes = R.drawable.ic_edit, onClick = { onClickEdit() })
        Spacer(modifier = Modifier.size(6.dp))
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
    val uiModel = WishItemDetailUiModel(
        id = 1L,
        name = "21SS SAGE SHIRT [4COLOR]",
        images = listOf("https://url.kr/8vwf1e", "https://url.kr/8vwf1e", "https://url.kr/8vwf1e"),
        price = 108000,
        notiDate = LocalDateTime(2024, 1, 13, 1, 13),
        notiType = NotiType.RESTOCK,
        site = "https://www.naver.com/",
        memo = "S사이즈 https://www.naver.com",
        folderId = 1L,
        folderName = "상의",
        isOwnedItem = false,
        createAt = LocalDateTime(2025, 3, 20, 2, 0),
        fetchState = WishBoardState.Success(Unit),
    )

    WishItemDetailScreen(
        uiModel = uiModel,
        navController = rememberNavController(),
        enabledShopButton = uiModel.site != null,
        onClickToggleMemoEdit = {},
        onMemoInputChanged = {},
        onClickSaveMemo = {},
        updateFolder = {},
        onClickShop = {},
        onClickEdit = {},
        onClickDelete = {},
        onClickBack = {},
        onClickImage = {},
        onClickFolder = {},
        updateItemOwnership = {},
    )
}

@Preview
@Composable
fun PreviewWishItemDetailScreenFirstLoading() {
    WishItemDetailScreen(
        uiModel = WishItemDetailUiModel(fetchState = WishBoardState.Loading),
        navController = rememberNavController(),
        enabledShopButton = false,
        onClickToggleMemoEdit = {},
        onMemoInputChanged = {},
        onClickSaveMemo = {},
        updateFolder = {},
        onClickShop = {},
        onClickEdit = {},
        onClickDelete = {},
        onClickBack = {},
        onClickImage = {},
        onClickFolder = {},
        updateItemOwnership = {},
    )
}
