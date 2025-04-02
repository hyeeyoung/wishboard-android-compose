package com.hyeeyoung.wishboard.presentation.upload.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardNarrowButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
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
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.upload.WishItemUploadViewModel
import com.hyeeyoung.wishboard.presentation.upload.model.UploadInputType
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemUploadUiModel
import com.hyeeyoung.wishboard.presentation.util.extension.createImageUri
import com.hyeeyoung.wishboard.presentation.util.extension.fromJson
import com.hyeeyoung.wishboard.presentation.util.extension.getCurrentTime
import com.hyeeyoung.wishboard.presentation.util.extension.makeValidPriceStr
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.util.extension.toJson
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

@Composable
fun WishUploadScreen(
    navController: NavController,
    itemDetail: WishItemDetail? = null,
    viewModel: WishItemUploadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val enteredAddFlow = itemDetail == null

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
                        uploadType = WishItemUploadType.MANUAL
                    ) { id ->
                        navController.navigate("${MainScreen.WishItemDetail.route}/$id") {
                            navController.safePopBackStack()
                        }
                    }
                }

                false -> {
                    viewModel.updateWishItem(context = context, itemId = itemDetail?.id) {
                        navController.safePopBackStack()
                    }
                }
            }
        },
        getFolders = {
            viewModel.getFolders(it)
        },
        onClickClose = {
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
        setNotiInfo = viewModel::setNotiInfo,
        onUriChange = { uri ->
            viewModel.setItemImageUrl(uri)
        }
    )
}

@Composable
fun WishUploadScreen(
    uiModel: WishItemUploadUiModel,
    enteredAddFlow: Boolean,
    onClickSave: () -> Unit,
    onClickClose: () -> Unit,
    getFolders: ((List<FolderItem>) -> Unit) -> Unit,
    onSelectFolder: (FolderItem?) -> Unit,
    onTextChange: (UploadInputType, String) -> Unit,
    setNotiInfo: (NotiInfo) -> Unit,
    onUriChange: (Uri?) -> Unit,
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_spin))

    var cameraUri: Uri? = null
    val albumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            Timber.e("uri : $it")
            onUriChange(it)
        }
    }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) onUriChange(cameraUri)
        }

    val isEnabledSave by remember(
        uiModel.itemName,
        uiModel.itemPrice,
        uiModel.itemImageUri,
        uiModel.downloadImageUrl
    ) {
        mutableStateOf(
            uiModel.itemName.isNotBlank()
                    && uiModel.itemPrice.isNotBlank()
                    && (uiModel.itemImageUri != null || !uiModel.downloadImageUrl.isNullOrBlank())
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

            is ModalData.Modal.Noti -> {
                setNotiInfo(data.notiInfo.fromJson<NotiInfo>())
            }

            is ModalData.Modal.FolderList -> {
                onSelectFolder(data.selectedFolder)
            }

            is ModalData.Modal.ShopLink -> {
                onTextChange(UploadInputType.ITEM_URL, data.link)
            }

            else -> {}
        }
    }
    val imageHeight = LocalConfiguration.current.screenWidthDp * 0.66
    val imageContainerShape = RoundedCornerShape(32.dp)

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
                onClickStartIcon = onClickClose,
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
                    iterations = LottieConstants.IterateForever
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WishBoardTheme.colors.white)
                    .padding(top = 6.dp + paddingValues.calculateTopPadding(), bottom = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight.dp)
                        .background(
                            color = WishBoardTheme.colors.gray100,
                            shape = imageContainerShape,
                        )
                        .clip(imageContainerShape)
                        .noRippleClickable {
                            ModalData.OptionModal.ImageSelection.openModal(context, modalLauncher)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxHeight(),
                        model = uiModel.itemImageUri ?: uiModel.downloadImageUrl,
                        contentDescription = null,
                    )

                    WishBoardIconButton(
                        iconRes = R.drawable.ic_camera,
                        onClick = {},
                    )
                }

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
                            ModalData.Modal.FolderList(selectedFolder = uiModel.selectedFolder, folders = folders)
                                .openModal(context, modalLauncher)
                        }
                    },
                )

                ItemInfoRow(
                    label = getNotiInfo(notiType = uiModel.itemNotiType, notiDate = uiModel.itemNotiDate)
                        ?: stringResource(id = R.string.wish_item_upload_noti),
                    onClickRow = {
                        ModalData.Modal.Noti(
                            NotiInfo(notiType = uiModel.itemNotiType, notiDate = uiModel.itemNotiDate).toJson()
                        ).openModal(context, modalLauncher)
                    },
                )

                ItemInfoRow(
                    label = uiModel.itemUrl.ifBlank { stringResource(id = R.string.wish_item_upload_shop_link) },
                    onClickRow = { ModalData.Modal.ShopLink(uiModel.itemUrl).openModal(context, modalLauncher) },
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
    }
}

@Composable
fun ItemInfoRow(label: String, guideText: String? = null, onClickRow: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onClickRow() }
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
        "[${
            stringResource(
                id = R.string.noti_item_type,
                formatArgs = arrayOf(notiType.label)
            )
        }] ${notiDate.getFormattedDateStr(WishBoardDateFormat.YY_M_D_A_H_MM)}"
    }

@Preview
@Composable
fun PreviewWishUploadScreen() {
    WishUploadScreen(
        uiModel = WishItemUploadUiModel(
            itemName = "21SS SAGE SHIRT [4COLOR]",
            downloadImageUrl = "https://url.kr/8vwf1e",
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
        setNotiInfo = {},
        onClickClose = {},
    )
}
