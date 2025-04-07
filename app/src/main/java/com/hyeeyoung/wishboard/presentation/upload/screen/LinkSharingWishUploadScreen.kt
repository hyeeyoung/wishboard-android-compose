package com.hyeeyoung.wishboard.presentation.upload.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.WishBoardSnackbarHost
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardMiniSingleTextField
import com.hyeeyoung.wishboard.designsystem.style.MontserratFamily
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.util.PriceTransformation
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiInfo
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.presentation.folder.FolderUploadModalContent
import com.hyeeyoung.wishboard.presentation.upload.model.UploadInputType
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemUploadUiModel
import com.hyeeyoung.wishboard.presentation.util.extension.fromJson
import com.hyeeyoung.wishboard.presentation.util.extension.makeValidPriceStr
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher
import com.hyeeyoung.wishboard.presentation.util.extension.toJson
import com.hyeeyoung.wishboard.presentation.util.extension.toNotiDateStr
import com.hyeeyoung.wishboard.presentation.util.safeLet
import kotlinx.datetime.LocalDateTime

private const val IMAGE_SIZE = 80

@Composable
fun LinkSharingWishUploadScreen(
    uiModel: WishItemUploadUiModel,
    snackbarHostState: SnackbarHostState,
    onTextChange: (UploadInputType, String) -> Unit,
    setNotiInfo: (NotiInfo) -> Unit,
    onSelectFolder: (FolderItem) -> Unit,
    onClickSave: () -> Unit,
    onClickClose: () -> Unit = {},
    createFolder: (name: String, afterSuccess: () -> Unit) -> Unit,
) {
    val context = LocalContext.current
    var modalData by remember { mutableStateOf<ModalData.Modal?>(null) }
    val modalLauncher = rememberModalLauncher { _, data ->
        when (data) {
            is ModalData.Modal.Noti -> {
                setNotiInfo(data.notiInfo.fromJson<NotiInfo>())
            }

            else -> {}
        }
    }

    Box {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .noRippleClickable { onClickClose() },
            )
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
                            modifier = Modifier.background(WishBoardTheme.colors.white),
                            iconRes = R.drawable.ic_close,
                            onClick = { onClickClose() },
                        )
                    }

                    Spacer(modifier = Modifier.size(7.dp))

                    val textFieldModifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                    WishBoardMiniSingleTextField(
                        modifier = textFieldModifier,
                        input = uiModel.itemName,
                        placeholder = stringResource(id = R.string.wish_item_link_sharing_upload_name),
                        onTextChange = { input ->
                            onTextChange(UploadInputType.ITEM_NAME, input)
                        },
                    )

                    WishBoardMiniSingleTextField(
                        modifier = textFieldModifier,
                        input = uiModel.itemPrice,
                        style = TextStyle(
                            fontFamily = MontserratFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        ),
                        placeholder = stringResource(id = R.string.wish_item_link_sharing_upload_price),
                        onTextChange = { input ->
                            onTextChange(UploadInputType.ITEM_PRICE, input.makeValidPriceStr() ?: "")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PriceTransformation(),
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Row(
                        modifier = Modifier
                            .noRippleClickable {
                                ModalData.Modal
                                    .Noti(
                                        NotiInfo(
                                            notiType = uiModel.itemNotiType,
                                            notiDate = uiModel.itemNotiDate
                                        ).toJson()
                                    )
                                    .openModal(context, modalLauncher)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(id = R.drawable.ic_notice),
                            contentDescription = null,
                            tint = WishBoardTheme.colors.gray700,
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = safeLet(uiModel.itemNotiType, uiModel.itemNotiDate) { type, date ->
                                "${date.toNotiDateStr()} ${type.label}"
                            } ?: stringResource(id = R.string.wish_item_link_sharing_upload_noti_setting),
                            style = WishBoardTheme.typography.suitD3,
                            color = WishBoardTheme.colors.gray700,
                        )

                        if (uiModel.itemNotiType != null) {
                            Spacer(modifier = Modifier.size(2.dp))
                            Icon(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clickable {
                                        setNotiInfo(NotiInfo(null, null))
                                    }
                                    .size(14.dp),
                                painter = painterResource(id = R.drawable.ic_delete_circle),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        }
                    }

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item {
                            NewFolder(isLogin = uiModel.isLogin, onClickNew = {
                                modalData = ModalData.Modal.NewFolder(folderName = "")
                            })
                        }
                        items(uiModel.folders) {
                            FolderItem(
                                isSelected = uiModel.selectedFolder == it,
                                folder = it,
                                onClickFolder = { folder -> onSelectFolder(folder) },
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }

                    val buttonTextRes =
                        if (uiModel.isLogin) {
                            R.string.wish_item_link_sharing_upload
                        } else {
                            R.string.wish_item_link_sharing_upload_after_login
                        }

                    WishBoardWideButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        enabled = uiModel.isLogin && uiModel.itemName.isNotBlank() && uiModel.itemPrice.isNotBlank(),
                        onClick = onClickSave,
                        text = stringResource(id = buttonTextRes),
                        state = uiModel.wishItemUploadState,
                    )
                }

                val imageModifier = Modifier
                    .size(IMAGE_SIZE.dp)
                    .clip(CircleShape)
                if (!uiModel.downloadImageUrl.isNullOrBlank()) {
                    Image(
                        modifier = imageModifier,
                        model = uiModel.downloadImageUrl,
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

        WishBoardSnackbarHost(
            modifier = Modifier
                .zIndex(2f)
                .navigationBarsPadding()
                .align(Alignment.BottomCenter),
            hostState = snackbarHostState
        )
    }

    WishBoardModal(
        isOpen = modalData != null,
        titleRes = modalData?.title,
        onDismissRequest = {
            modalData = null
        },
        content = {
            FolderUploadModalContent(
                folderName = null,
                uploadState = uiModel.folderAddState,
                existingFolderName = uiModel.existingFolderName,
                onClickComplete = { name ->
                    createFolder(name) { modalData = null }
                })
        }
    )
}

@Composable
fun FolderItem(
    isSelected: Boolean,
    folder: FolderItem,
    onClickFolder: (FolderItem) -> Unit,
) {
    Box(
        modifier = Modifier
            .size(IMAGE_SIZE.dp)
            .noRippleClickable { onClickFolder(folder) }
            .clip((RoundedCornerShape(10.dp))),
    ) {
        Image(
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun NewFolder(isLogin: Boolean, onClickNew: () -> Unit) {
    Column(
        modifier = Modifier
            .size(IMAGE_SIZE.dp)
            .noRippleClickable(enabled = isLogin) { onClickNew() }
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

@Preview(showSystemUi = true)
@Composable
fun PreviewLinkSharingWishUploadScreen() {
    LinkSharingWishUploadScreen(
        uiModel = WishItemUploadUiModel(
            isLogin = false,
            itemNotiType = NotiType.SALE_START,
            itemNotiDate = LocalDateTime(2024, 3, 22, 13, 0)
        ),
        snackbarHostState = SnackbarHostState(),
        onTextChange = { _, _ -> },
        setNotiInfo = { },
        onSelectFolder = {},
        onClickSave = {},
        onClickClose = {},
        createFolder = { _, _ -> },
    )
}
