package com.hyeeyoung.wishboard.presentation.upload.screen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardNarrowButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.ModalTitle
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
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
import com.hyeeyoung.wishboard.presentation.upload.model.UploadImage
import com.hyeeyoung.wishboard.presentation.upload.model.UploadInputType
import com.hyeeyoung.wishboard.presentation.util.extension.createImageUri
import com.hyeeyoung.wishboard.presentation.util.extension.fromJson
import com.hyeeyoung.wishboard.presentation.util.extension.getScheduleTimeFormat
import com.hyeeyoung.wishboard.presentation.util.extension.makeValidPriceStr
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher
import com.hyeeyoung.wishboard.presentation.util.extension.rippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.util.extension.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
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
    val uiModel by viewModel.manualUploadUiModel.collectAsStateWithLifecycle()
    val enteredAddFlow = itemDetail == null
    var modalData by remember { mutableStateOf<ModalData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { newState ->
        if (modalData !is ModalData.Modal.ShopLink) {
            newState != SheetValue.Hidden
        } else {
            true
        }
    })
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        keyboardController?.hide()
        navController.safePopBackStack()
    }

    LaunchedEffect(Unit) {
        viewModel.setTokenForProfileImageUri()
        viewModel.getFolders(uploadType = WishItemUploadType.MANUAL)
    }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    WishUploadScreen(
        uiModel = uiModel,
        enteredAddFlow = enteredAddFlow,
        modalData = modalData,
        coroutineScope = coroutineScope,
        sheetState = sheetState,
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
                        keyboardController?.hide()
                        navController.safePopBackStack()
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
        createFolder = { name ->
            viewModel.createFolder(folderName = name, uploadType = WishItemUploadType.MANUAL) {
                coroutineScope.launch { sheetState.hide() }
                modalData = null
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishUploadScreen(
    uiModel: ManualUploadItemUiModel,
    modalData: ModalData?,
    enteredAddFlow: Boolean,
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
    isValidNotiDate: (NotiInfo) -> Boolean,
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_spin))
    val keyboardController = LocalSoftwareKeyboardController.current

    var cameraUri: Uri? = null
    val albumLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGE_COUNT)) { uris ->
            Timber.e("uri : $uris")
            onUriChange(uris)
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
                    cameraLauncher.launch(cameraUri)
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
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiModel.wishItemUploadState is WishBoardState.Loading) {
                LottieAnimation(
                    modifier = Modifier
                        .size(100.dp)
                        .zIndex(2f)
                        .align(Alignment.Center),
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WishBoardTheme.colors.white)
                    .padding(top = 6.dp + paddingValues.calculateTopPadding(), bottom = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                ItemImageRow(
                    images = uiModel.images,
                    selectedImageCount = uiModel.images.size,
                    onClickDelete = deleteImage,
                    addImage = {
                        ModalData.OptionModal.ImageSelection.openModal(context, modalLauncher)
                    },
                )

                Column(modifier = Modifier.padding(top = 28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                            updateModalData(ModalData.Modal.NewFolder(folderName = ""))
                        },
                        showFolderDetail = {
                            updateModalData(
                                ModalData.Modal.FolderList(
                                    selectedFolder = uiModel.selectedFolder?.id?.let { FolderItem(id = it) },
                                    folders = uiModel.folders,
                                ),
                            )
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
                            updateModalData(
                                ModalData.Modal.Noti(
                                    NotiInfo(notiType = uiModel.itemNotiType, notiDate = uiModel.itemNotiDate).toJson(),
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
                            updateModalData(ModalData.Modal.ShopLink(uiModel.itemUrl.text))
                            coroutineScope.launch { sheetState.show() }
                        },
                    )

                    WishBoardDivider()

                    WishBoardLabelTextField(
                        modifier = inputFieldModifier,
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
                    )

                    Spacer(modifier = Modifier.size(64.dp))
                }
            }
        }

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
                            onClickComplete = { link ->
                                onTextChange(UploadInputType.ITEM_URL, TextFieldValue(link))
                                coroutineScope.launch { sheetState.hide() }
                                updateModalData(null)
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
    addImage: () -> Unit,
) {
    val containerSize = 100.dp
    val containerShape = RoundedCornerShape(10.dp)

    LazyRow(
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

        items(images) { image ->
            Box(
                modifier = Modifier
                    .size(containerSize)
                    .clip(containerShape),
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
            }
        }

        item {
            Spacer(modifier = Modifier.width(10.dp))
        }
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
                        .fillMaxHeight()
                        .padding(end = dimensionResource(id = R.dimen.spacing_base))
                        .rippleClickable {
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
    )
}

@Composable
private fun ItemFieldWithDetailIcon(
    modifier: Modifier = Modifier,
    label: String,
    value: String?,
    placeholder: String,
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
        createFolder = {},
    )
}
