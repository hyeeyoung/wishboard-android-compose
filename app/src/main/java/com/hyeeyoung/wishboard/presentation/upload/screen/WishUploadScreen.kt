package com.hyeeyoung.wishboard.presentation.upload.screen

import android.net.Uri
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardNarrowButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardTwoButtonDialog
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.ModalTitle
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.loading.ThreeDotsLoadingView
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardLabelTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.util.PriceVisualTransformation
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiInfo
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.getFormattedDateStr
import com.hyeeyoung.wishboard.presentation.folder.FolderListModalContent
import com.hyeeyoung.wishboard.presentation.folder.FolderUploadModalContent
import com.hyeeyoung.wishboard.presentation.noti.NotiModalContent
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardString
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.upload.WishItemUploadViewModel
import com.hyeeyoung.wishboard.presentation.upload.component.ShopLinkModalContent
import com.hyeeyoung.wishboard.presentation.upload.model.ManualUploadItemUiModel
import com.hyeeyoung.wishboard.presentation.upload.model.ParsedItemPreview
import com.hyeeyoung.wishboard.presentation.upload.model.UploadImage
import com.hyeeyoung.wishboard.presentation.upload.model.UploadInputType
import com.hyeeyoung.wishboard.presentation.util.extension.createImageUri
import com.hyeeyoung.wishboard.presentation.util.extension.fromJson
import com.hyeeyoung.wishboard.presentation.util.extension.getScheduleTimeFormat
import com.hyeeyoung.wishboard.presentation.util.extension.getValidUrl
import com.hyeeyoung.wishboard.presentation.util.extension.makeValidPriceStr
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher
import com.hyeeyoung.wishboard.presentation.util.extension.rippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.util.extension.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import timber.log.Timber

const val MAX_IMAGE_COUNT = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishUploadScreen(
    navController: NavController,
    itemDetail: WishItemDetail? = null,
    viewModel: WishItemUploadViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val view = LocalView.current
    val uiModel by viewModel.manualUploadUiModel.collectAsStateWithLifecycle()
    val enteredAddFlow = itemDetail == null
    var modalData by remember { mutableStateOf<ModalData?>(null) }
    var clipboardItemUrl by remember { mutableStateOf<String?>(null) }
    // 직전에 노출했던 링크와 동일하다면 포그라운드로 돌아와도 다시 노출하지 않는다.
    var lastCheckedClipboardUrl by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { true })
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        keyboardController?.hide()
        navController.safePopBackStack()
    }

    LaunchedEffect(Unit) {
        viewModel.setTokenForProfileImageUri()
        viewModel.getFolders(uploadType = WishItemUploadType.MANUAL)
    }

    // ON_RESUME 시점에는 아직 윈도우가 포커스를 되찾기 전이라 클립보드를 읽으면 null이 반환될 수 있어,
    // 윈도우가 실제로 포커스를 되찾는 시점(포그라운드 복귀 포함)에 맞춰 클립보드를 확인한다.
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) {
                val url = clipboardManager.getText()?.text?.getValidUrl()
                Timber.e("hello : $url")
                if (url != null && url != lastCheckedClipboardUrl) {
                    lastCheckedClipboardUrl = url
                    clipboardItemUrl = url
                }
            }
        }
        view.viewTreeObserver.addOnWindowFocusChangeListener(listener)
        onDispose { view.viewTreeObserver.removeOnWindowFocusChangeListener(listener) }
    }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    WishUploadScreen(
        uiModel = uiModel,
        enteredAddFlow = enteredAddFlow,
        modalData = modalData,
        clipboardItemUrl = clipboardItemUrl,
        coroutineScope = coroutineScope,
        sheetState = sheetState,
        onClickLoadItem = {
            clipboardItemUrl = null
            viewModel.getParsedWishItem(uploadType = WishItemUploadType.MANUAL, context = context, site = it)
        },
        onDismissClipboardItem = {
            clipboardItemUrl = null
        },
        onResolvePendingParsedItem = viewModel::resolvePendingParsedItem,
        onSelectFolder = { folder ->
            viewModel.updateSelectedFolder(folderItem = folder, uploadType = WishItemUploadType.MANUAL)
        },
        onClickSave = {
            when (enteredAddFlow) {
                true -> {
                    viewModel.uploadWishItemForManual(
                        context = context,
                    ) { id ->
                        navController.navigate("${MainScreen.WishItemDetail.route}/$id") {
                            keyboardController?.hide()
                            navController.safePopBackStack()
                        }
                    }
                }

                false -> {
                    viewModel.updateWishItem(context = context, itemId = itemDetail?.id) {
                        val detailRoute = "${MainScreen.WishItemDetail.route}/${itemDetail?.id}"
                        keyboardController?.hide()
                        navController.navigate(detailRoute) {
                            popUpTo(detailRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        },
        updateModalData = { modal ->
            modalData = modal
        },
        onClickClose = {
            keyboardController?.hide()
            navController.safePopBackStack()
        },
        onTextChange = { type, input ->
            when (type) {
                UploadInputType.ITEM_NAME -> viewModel.onItemNameChanged(
                    name = input,
                    uploadType = WishItemUploadType.MANUAL,
                )

                UploadInputType.ITEM_PRICE -> viewModel.onItemPriceChanged(
                    price = input,
                    uploadType = WishItemUploadType.MANUAL,
                )

                UploadInputType.ITEM_MEMO -> viewModel.onItemMemoChanged(memo = input)
                UploadInputType.ITEM_URL -> viewModel.setItemUrl(url = input)
            }
        },
        onUriChange = { uris ->
            viewModel.addItemImageUrl(uris)
        },
        isValidNotiDate = {
            viewModel.isValidNotiDate(
                notiInfo = it,
                uploadType = WishItemUploadType.MANUAL,
            )
        },
        deleteImage = viewModel::deleteImage,
        onMoveImage = viewModel::reorderImage,
        createFolder = { name ->
            viewModel.createFolder(folderName = name, uploadType = WishItemUploadType.MANUAL) {
                coroutineScope.launch { sheetState.hide() }
                modalData = null
            }
        },
        updateSnackbarMessage = viewModel::updateSnackbarMessage,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishUploadScreen(
    uiModel: ManualUploadItemUiModel,
    modalData: ModalData?,
    enteredAddFlow: Boolean,
    clipboardItemUrl: String? = null,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    updateModalData: (ModalData?) -> Unit,
    onClickSave: () -> Unit,
    onClickClose: () -> Unit,
    createFolder: (name: String) -> Unit,
    onSelectFolder: (FolderItem?) -> Unit,
    onTextChange: (UploadInputType, TextFieldValue) -> Unit,
    onUriChange: (List<Uri>) -> Unit,
    deleteImage: (id: String) -> Unit,
    onMoveImage: (fromIndex: Int, toIndex: Int) -> Unit,
    isValidNotiDate: (NotiInfo) -> Boolean,
    updateSnackbarMessage: (String) -> Unit,
    onClickLoadItem: (String) -> Unit = {},
    onDismissClipboardItem: () -> Unit = {},
    onResolvePendingParsedItem: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val systemUiController = rememberSystemUiController()
    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scrollState = rememberScrollState()
    // 클립보드 토스트에서 "불러오기"를 눌러 파싱하는 동안에만 전체 화면 로딩뷰를 보여준다.
    var isLoadingFromClipboard by remember { mutableStateOf(false) }

    LaunchedEffect(uiModel.parsedItemFetchState) {
        if (uiModel.parsedItemFetchState !is WishBoardState.Loading) {
            isLoadingFromClipboard = false
        }
    }

    var cameraUri: Uri? = null
    val albumLauncher =
        if (MAX_IMAGE_COUNT - uiModel.images.size <= 1) {
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    onUriChange(listOf(uri))
                }
            }
        } else {
            rememberLauncherForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGE_COUNT - uiModel.images.size),
            ) { uris ->
                Timber.e("uri : $uris")
                onUriChange(uris)
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                cameraUri?.let {
                    onUriChange(listOf(it))
                }
            }
        }

    val isEnabledSave by remember(
        uiModel.itemName,
        uiModel.itemPrice,
        uiModel.images,
    ) {
        mutableStateOf(
            uiModel.itemName.text.isNotBlank() &&
                uiModel.itemPrice.text.isNotBlank() &&
                (uiModel.images.isNotEmpty()),
        )
    }

    val spanStyle = WishBoardTheme.typography.suitB2.copy(color = WishBoardTheme.colors.green700).toSpanStyle()
    val inputFieldModifier = Modifier.padding(horizontal = dimensionResource(R.dimen.spacing_base))

    val modalLauncher = rememberModalLauncher { isTopOption, data ->
        when (data) {
            is ModalData.OptionModal.ImageSelection -> {
                if (isTopOption) {
                    cameraUri = context.createImageUri(uiModel.accessToken)
                    cameraUri?.let {
                        cameraLauncher.launch(it)
                    }
                } else {
                    albumLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }

            else -> {}
        }
    }

    SideEffect {
        systemUiController.setNavigationBarColor(color = Color.White)
    }

    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                startIcon = WishBoardTopBarModel.TopBarIcon.CLOSE,
                title = stringResource(
                    id = if (enteredAddFlow) {
                        R.string.wish_item_upload_add_title
                    } else {
                        R.string.wish_item_upload_edit_title
                    },
                ),
                onClickStartIcon = {
                    keyboardController?.hide()
                    onClickClose()
                },
            ),
            endComponent = { modifier ->
                Row(modifier = modifier) {
                    WishBoardNarrowButton(
                        enabled = isEnabledSave,
                        onClick = { onClickSave() },
                        text = stringResource(id = R.string.save),
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }
            },
        )
    }) { paddingValues ->
        // 폴더 리스트를 아직 조회하지 못한 최초 진입 시점에는 폼 전체를 로딩뷰로 대체한다.
        val isFolderListLoading = uiModel.folderFetchState !is WishBoardState.Success &&
            uiModel.folderFetchState !is WishBoardState.Failure

        Box(modifier = Modifier.fillMaxSize()) {
            if (isFolderListLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WishBoardTheme.colors.white),
                    contentAlignment = Alignment.Center,
                ) {
                    ThreeDotsLoadingView()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WishBoardTheme.colors.white)
                        .imePadding()
                        .verticalScroll(scrollState)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .padding(top = 6.dp + paddingValues.calculateTopPadding(), bottom = 16.dp),
                ) {
                    ItemImageRow(
                        images = uiModel.images,
                        selectedImageCount = uiModel.images.size,
                        onClickDelete = deleteImage,
                        onMoveImage = onMoveImage,
                        addImage = {
                            focusManager.clearFocus()
                            if (uiModel.images.size < MAX_IMAGE_COUNT) {
                                ModalData.OptionModal.ImageSelection.openModal(context, modalLauncher)
                            } else {
                                updateSnackbarMessage("앗, 이미지는 최대 10장까지만 등록할 수 있어요!")
                            }
                        },
                    )

                    Column(
                        modifier = Modifier.padding(top = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        WishBoardLabelTextField(
                            modifier = inputFieldModifier,
                            label = listOf(
                                WishBoardString.NormalString("상품명 "),
                                WishBoardString.SpanString("*"),
                            ),
                            spanStyle = spanStyle,
                            textFieldValue = uiModel.itemName,
                            placeholder = stringResource(id = R.string.wish_item_upload_item_name),
                            onTextChange = { input ->
                                onTextChange(UploadInputType.ITEM_NAME, input)
                            },
                        )

                        WishBoardDivider()

                        WishBoardLabelTextField(
                            modifier = inputFieldModifier,
                            label = listOf(
                                WishBoardString.NormalString("가격 "),
                                WishBoardString.SpanString("*"),
                            ),
                            spanStyle = spanStyle,
                            textFieldValue = uiModel.itemPrice,
                            placeholder = stringResource(id = R.string.wish_item_upload_item_price),
                            onTextChange = { input ->
                                onTextChange(
                                    UploadInputType.ITEM_PRICE,
                                    input.copy(text = input.text.makeValidPriceStr() ?: ""),
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = PriceVisualTransformation(),
                        )

                        WishBoardDivider()

                        FolderList(
                            modifier = inputFieldModifier,
                            folders = uiModel.folders,
                            selectedFolder = uiModel.selectedFolder,
                            onClickNewFolder = {
                                focusManager.clearFocus()
                                updateModalData(ModalData.Modal.NewFolder(folderName = ""))
                                coroutineScope.launch { sheetState.show() }
                            },
                            showFolderDetail = {
                                updateModalData(
                                    ModalData.Modal.FolderList(
                                        selectedFolder = uiModel.selectedFolder?.id?.let { FolderItem(id = it) },
                                        folders = uiModel.folders,
                                    ),
                                )
                                coroutineScope.launch { sheetState.show() }
                            },
                            onClickFolder = { folder ->
                                onSelectFolder(folder)
                            },
                        )

                        WishBoardDivider()

                        NotiField(
                            modifier = inputFieldModifier,
                            notiInfo = getNotiInfo(notiType = uiModel.itemNotiType, notiDate = uiModel.itemNotiDate),
                            onClick = {
                                focusManager.clearFocus()
                                updateModalData(
                                    ModalData.Modal.Noti(
                                        NotiInfo(
                                            notiType = uiModel.itemNotiType,
                                            notiDate = uiModel.itemNotiDate,
                                        ).toJson(),
                                    ),
                                )
                                coroutineScope.launch { sheetState.show() }
                            },
                        )

                        WishBoardDivider()

                        ShopField(
                            modifier = inputFieldModifier,
                            shopLink = uiModel.itemUrl.text,
                            onClick = {
                                focusManager.clearFocus()
                                updateModalData(ModalData.Modal.ShopLink(uiModel.itemUrl.text))
                                coroutineScope.launch { sheetState.show() }
                            },
                        )

                        WishBoardDivider()

                        WishBoardLabelTextField(
                            modifier = inputFieldModifier,
                            textFieldModifier = Modifier
                                .focusRequester(focusRequester),
                            label = listOf(
                                WishBoardString.NormalString("메모"),
                            ),
                            spanStyle = spanStyle,
                            textFieldValue = uiModel.itemMemo,
                            placeholder = stringResource(id = R.string.wish_item_upload_memo),
                            singleLine = false,
                            onTextChange = { input ->
                                onTextChange(UploadInputType.ITEM_MEMO, input)
                            },
                            onFocusChange = { isFocused ->
                                if (isFocused) {
                                    coroutineScope.launch {
                                        delay(1000L)
                                        bringIntoViewRequester.bringIntoView()
                                    }
                                }
                            },
                        )

                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }
            }

            if (uiModel.wishItemUploadState is WishBoardState.Loading ||
                (isLoadingFromClipboard && uiModel.parsedItemFetchState is WishBoardState.Loading)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(2f)
                        .background(WishBoardTheme.colors.white.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center,
                ) {
                    ThreeDotsLoadingView()
                }
            }

            if (clipboardItemUrl != null &&
                !isFolderListLoading &&
                uiModel.wishItemUploadState !is WishBoardState.Loading
            ) {
                ClipboardItemLoadToast(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .imePadding(),
                    isLoading = uiModel.parsedItemFetchState is WishBoardState.Loading,
                    onClickLoad = {
                        isLoadingFromClipboard = true
                        onClickLoadItem(clipboardItemUrl)
                    },
                    onDismiss = onDismissClipboardItem,
                )
            }
        }

        WishBoardTwoButtonDialog(
            dialogData = if (uiModel.pendingParsedItem != null) DialogData.OverwriteParsedItem else null,
            dismissOnConfirm = false,
            onClickConfirm = { onResolvePendingParsedItem(true) },
            onDismissRequest = { onResolvePendingParsedItem(false) },
        )

        WishBoardModal(
            isOpen = modalData != null,
            sheetState = sheetState,
            onDismissRequest = {
                updateModalData(null)
            },
            content = {
                when (modalData) {
                    is ModalData.Modal.Noti -> {
                        val notiData = (modalData as ModalData.Modal.Noti)
                        NotiModalContent(
                            notiInfo = notiData.notiInfo.fromJson<NotiInfo>(),
                            onClickComplete = { type, date ->
                                isValidNotiDate(NotiInfo(notiType = type, notiDate = date))
                                coroutineScope.launch { sheetState.hide() }
                                updateModalData(null)
                            },
                            onDismissRequest = {
                                coroutineScope.launch { sheetState.hide() }
                                updateModalData(null)
                            },
                        )
                    }

                    is ModalData.Modal.NewFolder -> {
                        Column {
                            ModalTitle(
                                title = stringResource(id = R.string.modal_new_folder_title),
                                onDismissRequest = {
                                    coroutineScope.launch { sheetState.hide() }
                                    updateModalData(null)
                                },
                            )

                            FolderUploadModalContent(
                                folderName = null,
                                uploadState = uiModel.folderAddState,
                                existingFolderName = uiModel.existingFolderName,
                                onClickComplete = { name ->
                                    createFolder(name)
                                },
                            )
                        }
                    }

                    is ModalData.Modal.FolderList -> {
                        val folderData = (modalData as ModalData.Modal.FolderList)
                        FolderListModalContent(
                            selectedFolder = folderData.selectedFolder,
                            folders = folderData.folders,
                            onClickFolder = { folder ->
                                onSelectFolder(folder)
                                coroutineScope.launch { sheetState.hide() }
                                updateModalData(null)
                            },
                            onDismissRequest = {
                                coroutineScope.launch { sheetState.hide() }
                                updateModalData(null)
                            },
                        )
                    }

                    is ModalData.Modal.ShopLink -> {
                        val linkData = (modalData as ModalData.Modal.ShopLink)
                        ShopLinkModalContent(
                            link = linkData.link,
                            isLoadingItem = uiModel.parsedItemFetchState is WishBoardState.Loading,
                            onClickComplete = { link ->
                                onTextChange(UploadInputType.ITEM_URL, TextFieldValue(link))
                                coroutineScope.launch { sheetState.hide() }
                                updateModalData(null)
                            },
                            onClickLoadItem = { link ->
                                onClickLoadItem(link)
                            },
                            onDismissRequest = {
                                coroutineScope.launch { sheetState.hide() }
                                updateModalData(null)
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
fun ItemImageRow(
    images: List<UploadImage>,
    selectedImageCount: Int,
    onClickDelete: (id: String) -> Unit,
    onMoveImage: (fromIndex: Int, toIndex: Int) -> Unit,
    addImage: () -> Unit,
) {
    val containerSize = 100.dp
    val containerShape = RoundedCornerShape(10.dp)

    // 카메라 아이콘이 LazyRow의 첫 item으로 존재하므로, 라이브러리가 넘기는 전역 인덱스에서 1을 빼면 images 리스트의 로컬 인덱스가 된다.
    val cameraItemCount = 1
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = (from.index - cameraItemCount).coerceIn(images.indices)
        val toIndex = (to.index - cameraItemCount).coerceIn(images.indices)
        onMoveImage(fromIndex, toIndex)
    }

    LazyRow(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_base)))

            Column(
                modifier = Modifier
                    .size(containerSize)
                    .clip(containerShape)
                    .background(Color(0xFFF3F3F3))
                    .rippleClickable {
                        addImage()
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_item_upload_camera),
                    tint = Color.Unspecified,
                    contentDescription = "카메라 아이콘",
                )

                Text(
                    modifier = Modifier.padding(top = 6.dp),
                    text = "$selectedImageCount/$MAX_IMAGE_COUNT",
                    style = WishBoardTheme.typography.suitD3,
                    color = WishBoardTheme.colors.gray200,
                    textAlign = TextAlign.Center,
                )
            }
        }

        itemsIndexed(images, key = { _, image -> image.id }) { index, image ->
            ReorderableItem(reorderableLazyListState, key = image.id) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 2.dp else 0.dp)
                Box(
                    modifier = Modifier
                        .size(containerSize)
                        .shadow(elevation, containerShape)
                        .clip(containerShape)
                        .longPressDraggableHandle(),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .rippleClickable {
                                onClickDelete(image.id)
                            }
                            .padding(5.dp)
                            .size(16.dp)
                            .zIndex(2f)
                            .border(width = 0.5.dp, shape = CircleShape, color = WishBoardTheme.colors.white)
                            .background(color = WishBoardTheme.colors.gray700, shape = CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete_small),
                            tint = Color.Unspecified,
                            contentDescription = "이미지 삭제 아이콘",
                        )
                    }

                    Image(
                        modifier = Modifier.size(containerSize),
                        model = when (image) {
                            is UploadImage.Remote -> image.url
                            is UploadImage.Local -> image.uri
                        },
                        alphaColor = WishBoardTheme.colors.gray700.copy(alpha = 0.05f),
                        contentDescription = null,
                    )

                    if (index == 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 3.dp),
                                text = "대표 사진",
                                style = WishBoardTheme.typography.suitD3,
                                color = WishBoardTheme.colors.white,
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
private fun ClipboardItemLoadToast(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onClickLoad: () -> Unit,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(5000L)
        onDismiss()
    }

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(color = WishBoardTheme.colors.gray600, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 6.dp)
            .padding(start = 16.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.clipboard_item_load_toast_message),
            style = WishBoardTheme.typography.suitB5,
            color = WishBoardTheme.colors.gray50,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .noRippleClickable(enabled = !isLoading, onClick = onClickLoad),
            text = stringResource(R.string.item_load_btn_text),
            style = WishBoardTheme.typography.suitB5,
            color = WishBoardTheme.colors.green500,
        )
    }
}

@Composable
fun getNotiInfo(notiType: NotiType?, notiDate: LocalDateTime?): String? =
    if (notiType == null || notiDate == null) {
        null
    } else {
        "${notiDate.getFormattedDateStr(WishBoardDateFormat.YY_M_D)} " +
            "${notiDate.getScheduleTimeFormat()} ${notiType.label}"
    }

@Composable
private fun FolderList(
    modifier: Modifier = Modifier,
    folders: List<FolderItem>,
    selectedFolder: FolderItem?,
    showFolderDetail: () -> Unit,
    onClickFolder: (FolderItem) -> Unit,
    onClickNewFolder: () -> Unit,
) {
    val itemShape = RoundedCornerShape(16.dp)
    val itemPadding = PaddingValues(vertical = 6.dp, horizontal = 10.dp)
    val density = LocalDensity.current
    val textMeasure = rememberTextMeasurer()
    val textStyle = WishBoardTheme.typography.suitB5
    val folderItemHeight = density.run {
        textMeasure.measure(
            "폴더",
            textStyle,
        ).size.width.toDp()
    } + itemPadding.calculateTopPadding() + itemPadding.calculateBottomPadding()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            modifier = modifier,
            text = stringResource(id = R.string.folder),
            color = WishBoardTheme.colors.gray700,
            style = WishBoardTheme.typography.suitB2,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item {
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_base)))

                        Box(
                            modifier = Modifier
                                .clip(itemShape)
                                .rippleClickable {
                                    onClickNewFolder()
                                }
                                .border(width = 1.dp, color = WishBoardTheme.colors.gray100, shape = itemShape)
                                .padding(itemPadding),
                        ) {
                            Text(
                                text = "+ 새 폴더",
                                style = WishBoardTheme.typography.suitH5,
                                color = WishBoardTheme.colors.gray600,
                            )
                        }
                    }

                    items(folders) { folder ->
                        val isSelected = folder.id == selectedFolder?.id

                        Box(
                            modifier = Modifier
                                .clip(itemShape)
                                .background(
                                    if (!isSelected) {
                                        WishBoardTheme.colors.gray50
                                    } else {
                                        WishBoardTheme.colors.gray600
                                    },
                                )
                                .rippleClickable {
                                    onClickFolder(folder)
                                }
                                .padding(itemPadding),
                        ) {
                            Text(
                                text = folder.name,
                                style = textStyle,
                                color = if (!isSelected) {
                                    WishBoardTheme.colors.gray200
                                } else {
                                    WishBoardTheme.colors.gray50
                                },
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .zIndex(2f)
                        .width(16.dp)
                        .height(folderItemHeight)
                        .align(Alignment.CenterEnd)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x80FFFFFF), // 50%
                                    Color(0xE6FFFFFF), // 90%
                                    Color.White,
                                ),
                            ),
                        ),
                )
            }

            if (folders.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(end = dimensionResource(id = R.dimen.spacing_base))
                        .noRippleClickable {
                            showFolderDetail()
                        },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_detail),
                        tint = Color.Unspecified,
                        contentDescription = "상세보기",
                    )
                }
            }
        }
    }
}

@Composable
private fun NotiField(
    modifier: Modifier = Modifier,
    notiInfo: String?,
    onClick: () -> Unit,
) {
    ItemFieldWithDetailIcon(
        modifier = modifier,
        label = "상품 일정",
        placeholder = "재입고, 세일 시작 등 일정 알림을 받아보세요.",
        value = notiInfo,
        onClick = onClick,
    )
}

@Composable
private fun ShopField(
    modifier: Modifier = Modifier,
    shopLink: String?,
    onClick: () -> Unit,
) {
    ItemFieldWithDetailIcon(
        modifier = modifier,
        label = stringResource(id = R.string.wish_item_upload_shop_link),
        placeholder = "쇼핑몰 링크를 추가해 보세요.",
        value = shopLink,
        onClick = onClick,
        overflow = TextOverflow.Ellipsis,
        maxLine = 1,
    )
}

@Composable
private fun ItemFieldWithDetailIcon(
    modifier: Modifier = Modifier,
    label: String,
    value: String?,
    placeholder: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLine: Int = Int.MAX_VALUE,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.noRippleClickable {
            onClick()
        },
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Text(
            modifier = modifier,
            text = label,
            color = WishBoardTheme.colors.gray700,
            style = WishBoardTheme.typography.suitB2,
        )

        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = if (value.isNullOrEmpty()) placeholder else value,
                style = WishBoardTheme.typography.suitD1,
                color = if (value.isNullOrEmpty()) WishBoardTheme.colors.gray200 else WishBoardTheme.colors.gray700,
                overflow = overflow,
                maxLines = maxLine,
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_detail),
                tint = Color.Unspecified,
                contentDescription = "상세보기",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewWishUploadScreen() {
    WishUploadScreen(
        uiModel = ManualUploadItemUiModel(
            itemName = TextFieldValue("21SS SAGE SHIRT [4COLOR]"),
            images = listOf(
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
            ),
            folders = listOf(
                FolderItem(id = 1L, name = "상의"),
                FolderItem(id = 2L, name = "하의"),
                FolderItem(id = 3L, name = "잡화 xptmxm gkrpTtmqslek."),
            ),
            itemPrice = TextFieldValue("108000"),
            itemNotiDate = LocalDateTime(2025, 8, 9, 12, 30),
            itemNotiType = NotiType.RESTOCK,
            itemUrl = TextFieldValue("https://www.naver.com/"),
            itemMemo = TextFieldValue(""),
            selectedFolder = FolderItem(id = 1L, name = "상의"),
            folderFetchState = WishBoardState.Success(Unit),
        ),
        modalData = null,
        sheetState = rememberModalBottomSheetState(),
        coroutineScope = rememberCoroutineScope(),
        enteredAddFlow = false,
        updateModalData = {},
        onTextChange = { _, _ -> },
        onSelectFolder = {},
        onUriChange = {},
        onClickSave = {},
        onClickClose = {},
        isValidNotiDate = { true },
        deleteImage = {},
        onMoveImage = { _, _ -> },
        createFolder = {},
        updateSnackbarMessage = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewWishUploadScreenFolderListLoading() {
    WishUploadScreen(
        uiModel = ManualUploadItemUiModel(
            folderFetchState = WishBoardState.Loading,
        ),
        modalData = null,
        sheetState = rememberModalBottomSheetState(),
        coroutineScope = rememberCoroutineScope(),
        enteredAddFlow = true,
        updateModalData = {},
        onTextChange = { _, _ -> },
        onSelectFolder = {},
        onUriChange = {},
        onClickSave = {},
        onClickClose = {},
        isValidNotiDate = { true },
        deleteImage = {},
        onMoveImage = { _, _ -> },
        createFolder = {},
        updateSnackbarMessage = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewWishUploadScreenSaving() {
    WishUploadScreen(
        uiModel = ManualUploadItemUiModel(
            itemName = TextFieldValue("21SS SAGE SHIRT [4COLOR]"),
            images = listOf(
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
            ),
            folders = listOf(
                FolderItem(id = 1L, name = "상의"),
                FolderItem(id = 2L, name = "하의"),
                FolderItem(id = 3L, name = "잡화 xptmxm gkrpTtmqslek."),
            ),
            itemPrice = TextFieldValue("108000"),
            itemNotiDate = LocalDateTime(2025, 8, 9, 12, 30),
            itemNotiType = NotiType.RESTOCK,
            itemUrl = TextFieldValue("https://www.naver.com/"),
            itemMemo = TextFieldValue(""),
            selectedFolder = FolderItem(id = 1L, name = "상의"),
            folderFetchState = WishBoardState.Success(Unit),
            wishItemUploadState = WishBoardState.Loading,
        ),
        modalData = null,
        sheetState = rememberModalBottomSheetState(),
        coroutineScope = rememberCoroutineScope(),
        enteredAddFlow = false,
        updateModalData = {},
        onTextChange = { _, _ -> },
        onSelectFolder = {},
        onUriChange = {},
        onClickSave = {},
        onClickClose = {},
        isValidNotiDate = { true },
        deleteImage = {},
        onMoveImage = { _, _ -> },
        createFolder = {},
        updateSnackbarMessage = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewWishUploadScreenClipboardItemLoadToast() {
    WishUploadScreen(
        uiModel = ManualUploadItemUiModel(
            folderFetchState = WishBoardState.Success(Unit),
        ),
        modalData = null,
        clipboardItemUrl = "https://www.musinsa.com/app/goods/2377269",
        sheetState = rememberModalBottomSheetState(),
        coroutineScope = rememberCoroutineScope(),
        enteredAddFlow = true,
        updateModalData = {},
        onTextChange = { _, _ -> },
        onSelectFolder = {},
        onUriChange = {},
        onClickSave = {},
        onClickClose = {},
        isValidNotiDate = { true },
        deleteImage = {},
        onMoveImage = { _, _ -> },
        createFolder = {},
        updateSnackbarMessage = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewWishUploadScreenOverwriteParsedItemDialog() {
    WishUploadScreen(
        uiModel = ManualUploadItemUiModel(
            itemName = TextFieldValue("21SS SAGE SHIRT [4COLOR]"),
            itemPrice = TextFieldValue("108000"),
            folderFetchState = WishBoardState.Success(Unit),
            pendingParsedItem = ParsedItemPreview(
                itemName = "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
                itemPrice = "59000",
                imageUrl = "https://url.kr/8vwf1e",
                site = "https://www.musinsa.com/app/goods/2377269",
            ),
        ),
        modalData = null,
        sheetState = rememberModalBottomSheetState(),
        coroutineScope = rememberCoroutineScope(),
        enteredAddFlow = true,
        updateModalData = {},
        onTextChange = { _, _ -> },
        onSelectFolder = {},
        onUriChange = {},
        onClickSave = {},
        onClickClose = {},
        isValidNotiDate = { true },
        deleteImage = {},
        onMoveImage = { _, _ -> },
        createFolder = {},
        updateSnackbarMessage = {},
    )
}
