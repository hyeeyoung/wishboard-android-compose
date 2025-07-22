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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardSimpleTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.util.PriceTransformation
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiInfo
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.getFormattedDateStr
import com.hyeeyoung.wishboard.presentation.folder.FolderListModalContent
import com.hyeeyoung.wishboard.presentation.noti.NotiModalContent
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.upload.WishItemUploadViewModel
import com.hyeeyoung.wishboard.presentation.upload.component.ShopLinkModalContent
import com.hyeeyoung.wishboard.presentation.upload.model.ManualUploadItemUiModel
import com.hyeeyoung.wishboard.presentation.upload.model.UploadImage
import com.hyeeyoung.wishboard.presentation.upload.model.UploadInputType
import com.hyeeyoung.wishboard.presentation.util.extension.createImageUri
import com.hyeeyoung.wishboard.presentation.util.extension.fromJson
import com.hyeeyoung.wishboard.presentation.util.extension.getCurrentTime
import com.hyeeyoung.wishboard.presentation.util.extension.makeValidPriceStr
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher
import com.hyeeyoung.wishboard.presentation.util.extension.rippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.util.extension.toJson
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

const val MAX_IMAGE_COUNT = 10

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

    BackHandler {
        keyboardController?.hide()
        navController.safePopBackStack()
    }

    LaunchedEffect(Unit) {
        viewModel.setTokenForProfileImageUri()
    }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    WishUploadScreen(
        uiModel = uiModel,
        enteredAddFlow = enteredAddFlow,
        onSelectFolder = { folder ->
            viewModel.updateSelectedFolder(folder)
        },
        onClickSave = {
            when (enteredAddFlow) {
                true -> {
                    viewModel.uploadWishItem(
                        context = context,
                        uploadType = WishItemUploadType.MANUAL,
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
        getFolders = {
            viewModel.getFolders(it)
        },
        onClickClose = {
            keyboardController?.hide()
            navController.safePopBackStack()
        },
        onTextChange = { type, input ->
            when (type) {
                UploadInputType.ITEM_NAME -> viewModel.onItemNameChanged(input)
                UploadInputType.ITEM_PRICE -> viewModel.onItemPriceChanged(input)
                UploadInputType.ITEM_MEMO -> viewModel.onItemMemoChanged(input)
                UploadInputType.ITEM_URL -> viewModel.setItemUri(input)
            }
        },
        onUriChange = { uris ->
            viewModel.addItemImageUrl(uris)
        },
        isValidNotiDate = viewModel::isValidNotiDate,
        deleteImage = viewModel::deleteImage,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishUploadScreen(
    uiModel: ManualUploadItemUiModel,
    enteredAddFlow: Boolean,
    onClickSave: () -> Unit,
    onClickClose: () -> Unit,
    getFolders: ((List<FolderItem>) -> Unit) -> Unit,
    onSelectFolder: (FolderItem?) -> Unit,
    onTextChange: (UploadInputType, String) -> Unit,
    onUriChange: (List<Uri>) -> Unit,
    deleteImage: (id: String) -> Unit,
    isValidNotiDate: (NotiInfo) -> Boolean,
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_spin))
    val keyboardController = LocalSoftwareKeyboardController.current
    var modalData by remember { mutableStateOf<ModalData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { newState ->
        if (modalData !is ModalData.Modal.ShopLink) {
            newState != SheetValue.Hidden
        } else {
            true
        }
    })
    val coroutineScope = rememberCoroutineScope()

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
            uiModel.itemName.isNotBlank() &&
                uiModel.itemPrice.isNotBlank() &&
                (uiModel.images.isNotEmpty()),
        )
    }

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

                WishBoardSimpleTextField(
                    input = uiModel.itemName,
                    placeholder = stringResource(id = R.string.wish_item_upload_item_name),
                    onTextChange = { input ->
                        onTextChange(UploadInputType.ITEM_NAME, input)
                    },
                )

                WishBoardSimpleTextField(
                    input = uiModel.itemPrice,
                    placeholder = stringResource(id = R.string.wish_item_upload_item_price),
                    onTextChange = { input ->
                        onTextChange(UploadInputType.ITEM_PRICE, input.makeValidPriceStr() ?: "")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PriceTransformation(prefix = "₩ "),
                )

                ItemInfoRow(
                    label = uiModel.selectedFolder?.name ?: stringResource(id = R.string.folder),
                    onClickRow = {
                        getFolders { folders ->
                            modalData =
                                ModalData.Modal.FolderList(selectedFolder = uiModel.selectedFolder, folders = folders)
                            coroutineScope.launch { sheetState.show() }
                        }
                    },
                )

                ItemInfoRow(
                    label = getNotiInfo(notiType = uiModel.itemNotiType, notiDate = uiModel.itemNotiDate)
                        ?: stringResource(id = R.string.wish_item_upload_noti),
                    onClickRow = {
                        modalData = ModalData.Modal.Noti(
                            NotiInfo(notiType = uiModel.itemNotiType, notiDate = uiModel.itemNotiDate).toJson(),
                        )
                        coroutineScope.launch { sheetState.show() }
                    },
                )

                ItemInfoRow(
                    label = uiModel.itemUrl.ifBlank { stringResource(id = R.string.wish_item_upload_shop_link) },
                    onClickRow = {
                        modalData = ModalData.Modal.ShopLink(uiModel.itemUrl)
                        coroutineScope.launch { sheetState.show() }
                    },
                )

                WishBoardSimpleTextField(
                    input = uiModel.itemMemo,
                    placeholder = stringResource(id = R.string.wish_item_upload_memo),
                    singleLine = false,
                    onTextChange = { input ->
                        onTextChange(UploadInputType.ITEM_MEMO, input)
                    },
                )
                Spacer(modifier = Modifier.size(64.dp))
            }
        }

        WishBoardModal(
            isOpen = modalData != null,
            sheetState = sheetState,
            onDismissRequest = {
                modalData = null
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
                                modalData = null
                            },
                            onDismissRequest = {
                                coroutineScope.launch { sheetState.hide() }
                                modalData = null
                            },
                        )
                    }

                    is ModalData.Modal.FolderList -> {
                        val folderData = (modalData as ModalData.Modal.FolderList)
                        FolderListModalContent(
                            selectedFolder = folderData.selectedFolder,
                            folders = folderData.folders,
                            onClickFolder = { folder ->
                                onSelectFolder(folder)
                                coroutineScope.launch { sheetState.hide() }
                                modalData = null
                            },
                            onDismissRequest = {
                                coroutineScope.launch { sheetState.hide() }
                                modalData = null
                            },
                        )
                    }

                    is ModalData.Modal.ShopLink -> {
                        val linkData = (modalData as ModalData.Modal.ShopLink)
                        ShopLinkModalContent(
                            link = linkData.link,
                            onClickComplete = { link ->
                                onTextChange(UploadInputType.ITEM_URL, link)
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
            .padding(top = 18.dp)
            .padding(start = dimensionResource(id = R.dimen.spacing_base)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
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
fun ItemInfoRow(label: String, guideText: String? = null, onClickRow: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onClickRow() }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
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
        "[${
            stringResource(
                id = R.string.noti_item_type,
                formatArgs = arrayOf(notiType.label),
            )
        }] ${notiDate.getFormattedDateStr(WishBoardDateFormat.YY_M_D_A_H_MM)}"
    }

@Preview
@Composable
fun PreviewWishUploadScreen() {
    WishUploadScreen(
        uiModel = ManualUploadItemUiModel(
            itemName = "21SS SAGE SHIRT [4COLOR]",
            images = listOf(
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
                UploadImage.Remote(url = "https://url.kr/8vwf1e"),
            ),
            itemPrice = "108000",
            itemNotiDate = getCurrentTime(),
            itemNotiType = NotiType.RESTOCK,
            itemUrl = "https://www.naver.com/",
            itemMemo = "",
            selectedFolder = FolderItem(id = 1L, name = "상의"),
        ),
        enteredAddFlow = false,
        getFolders = {},
        onTextChange = { _, _ -> },
        onSelectFolder = {},
        onUriChange = {},
        onClickSave = {},
        onClickClose = {},
        isValidNotiDate = { true },
        deleteImage = {},
    )
}
