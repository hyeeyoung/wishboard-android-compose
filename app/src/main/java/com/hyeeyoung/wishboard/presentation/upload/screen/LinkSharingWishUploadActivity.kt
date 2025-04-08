package com.hyeeyoung.wishboard.presentation.upload.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.LocalSnackbarHostState
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.noti.NotiInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.presentation.folder.FolderUploadModalContent
import com.hyeeyoung.wishboard.presentation.noti.NotiModalContent
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.WishBoardSnackbarVisuals
import com.hyeeyoung.wishboard.presentation.upload.WishItemUploadViewModel
import com.hyeeyoung.wishboard.presentation.upload.model.UploadInputType
import com.hyeeyoung.wishboard.presentation.util.extension.fromJson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@AndroidEntryPoint
class LinkSharingWishUploadActivity : ComponentActivity() {
    private val viewModel: WishItemUploadViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var url = ""
        if (intent.action == Intent.ACTION_SEND) {
            if (intent.type == "text/plain") {
                url = intent.getStringExtra(Intent.EXTRA_TEXT) ?: throw NullPointerException("Url is null")
                viewModel.getParsedWishItem(url)
                viewModel.getFolders {}
            }
        }

        setContent {
            val context = LocalContext.current
            val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
            val systemUiController = rememberSystemUiController()
            val coroutineScope = rememberCoroutineScope()
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            var modalData by remember { mutableStateOf<ModalData.Modal?>(null) }

            CompositionLocalProvider(LocalSnackbarHostState provides wishBoardSnackbarHostState) {
                val snackbarHostState = LocalSnackbarHostState.current

                SideEffect {
                    systemUiController.setNavigationBarColor(Color.White)
                }

                WishBoardSnackbarMessage(
                    snackbarHostState = snackbarHostState,
                    snackbarChannel = viewModel.globalSnackbarChannel
                )

                WishBoardGlobalSnackbarMessage(
                    snackbarChannel = viewModel.snackBarChannel,
                    sendSnackbarChannel = {
                        sendSnackbarVisualChannel(it)
                    })

                LinkSharingWishUploadScreen(
                    uiModel = uiModel,
                    snackbarHostState = snackbarHostState,
                    updateModalData = {
                        modalData = it
                        coroutineScope.launch { sheetState.show() }
                    },
                    onTextChange = { type, input ->
                        when (type) {
                            UploadInputType.ITEM_NAME -> viewModel.onItemNameChanged(input)
                            UploadInputType.ITEM_PRICE -> viewModel.onItemPriceChanged(input)
                            else -> {}
                        }
                    },
                    setNotiInfo = viewModel::setNotiInfo,
                    onSelectFolder = {
                        viewModel.updateSelectedFolder(it)
                    },
                    onClickSave = {
                        viewModel.uploadWishItem(
                            context = context,
                            uploadType = WishItemUploadType.PARSING,
                            afterSuccess = {
                                finish()
                            })
                    },
                    onClickClose = { finish() },
                )
            }

            WishBoardModal(
                isOpen = modalData != null,
                sheetState = sheetState,
                onDismissRequest = {
                    modalData = null
                },
                content = {
                    when (modalData) {
                        is ModalData.Modal.NewFolder -> {
                            Column {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        modifier = Modifier
                                            .padding(top = 16.dp)
                                            .align(Alignment.TopCenter),
                                        text = stringResource(id = R.string.modal_new_folder_title),
                                        style = WishBoardTheme.typography.suitH3,
                                        color = WishBoardTheme.colors.gray700,
                                    )

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = 5.dp, end = 8.dp),
                                    ) {
                                        WishBoardIconButton(
                                            iconRes = R.drawable.ic_close,
                                            onClick = {
                                                coroutineScope.launch { sheetState.hide() }
                                                modalData = null
                                            },
                                        )
                                    }
                                }

                                FolderUploadModalContent(
                                    folderName = null,
                                    uploadState = uiModel.folderAddState,
                                    existingFolderName = uiModel.existingFolderName,
                                    onClickComplete = { name ->
                                        viewModel.createFolder(name) {
                                            coroutineScope.launch { sheetState.hide() }
                                            modalData = null
                                        }
                                    })
                            }
                        }

                        is ModalData.Modal.Noti -> {
                            val notiData = (modalData as ModalData.Modal.Noti)


                            NotiModalContent(
                                notiInfo = notiData.notiInfo.fromJson<NotiInfo>(),
                                onClickComplete = { type, date ->
                                    viewModel.isValidNotiDate(NotiInfo(notiType = type, notiDate = date))
                                    coroutineScope.launch { sheetState.hide() }
                                    modalData = null
                                },
                                onDismissRequest = {
                                    coroutineScope.launch { sheetState.hide() }
                                    modalData = null
                                }
                            )
                        }

                        else -> {}
                    }
                }
            )
        }
    }

    private fun sendSnackbarVisualChannel(snackbarVisuals: WishBoardSnackbarVisuals) {
        viewModel.sendSnackbarChannel(snackbarVisuals)
    }

    companion object {
        var wishBoardSnackbarHostState: SnackbarHostState = SnackbarHostState()
    }
}
