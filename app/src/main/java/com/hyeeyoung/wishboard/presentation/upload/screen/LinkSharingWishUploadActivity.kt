package com.hyeeyoung.wishboard.presentation.upload.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.LocalSnackbarHostState
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.ModalTitle
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
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
                viewModel.getFolders(uploadType = WishItemUploadType.PARSING)
            }
        }

        setContent {
            val context = LocalContext.current
            val uiModel by viewModel.parsingUiModel.collectAsStateWithLifecycle()
            val systemUiController = rememberSystemUiController()
            val coroutineScope = rememberCoroutineScope()
            var modalData by remember { mutableStateOf<ModalData.Modal?>(null) }
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { newState ->
                    if (modalData is ModalData.Modal.Noti) {
                        newState != SheetValue.Hidden
                    } else {
                        true
                    }
                },
            )

            CompositionLocalProvider(LocalSnackbarHostState provides wishBoardSnackbarHostState) {
                val snackbarHostState = LocalSnackbarHostState.current

                SideEffect {
                    systemUiController.setNavigationBarColor(Color.White)
                }

                WishBoardSnackbarMessage(
                    snackbarHostState = snackbarHostState,
                    snackbarChannel = viewModel.globalSnackbarChannel,
                )

                WishBoardGlobalSnackbarMessage(
                    snackbarChannel = viewModel.snackBarChannel,
                    sendSnackbarChannel = {
                        sendSnackbarVisualChannel(it)
                    },
                )

                LinkSharingWishUploadScreen(
                    uiModel = uiModel,
                    snackbarHostState = snackbarHostState,
                    updateModalData = {
                        modalData = it
                        coroutineScope.launch { sheetState.show() }
                    },
                    onTextChange = { type, input ->
                        when (type) {
                            UploadInputType.ITEM_NAME -> viewModel.onItemNameChanged(
                                name = input,
                                uploadType = WishItemUploadType.PARSING,
                            )
                            UploadInputType.ITEM_PRICE -> viewModel.onItemPriceChanged(
                                price = input,
                                uploadType = WishItemUploadType.PARSING,
                            )
                            else -> {}
                        }
                    },
                    setNotiInfo = { viewModel.setNotiInfo(notiInfo = it, uploadType = WishItemUploadType.PARSING) },
                    onSelectFolder = {
                        viewModel.updateSelectedFolder(folderItem = it, uploadType = WishItemUploadType.PARSING)
                    },
                    onClickSave = {
                        viewModel.uploadWishItemForParsing(
                            context = context,
                            afterSuccess = {
                                finish()
                            },
                        )
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
                                ModalTitle(
                                    title = stringResource(id = R.string.modal_new_folder_title),
                                    onDismissRequest = {
                                        coroutineScope.launch { sheetState.hide() }
                                        modalData = null
                                    },
                                )

                                FolderUploadModalContent(
                                    folderName = null,
                                    uploadState = uiModel.folderAddState,
                                    existingFolderName = uiModel.existingFolderName,
                                    onClickComplete = { name ->
                                        viewModel.createFolder(
                                            folderName = name,
                                            uploadType = WishItemUploadType.PARSING,
                                        ) {
                                            coroutineScope.launch { sheetState.hide() }
                                            modalData = null
                                        }
                                    },
                                )
                            }
                        }

                        is ModalData.Modal.Noti -> {
                            val notiData = (modalData as ModalData.Modal.Noti)

                            NotiModalContent(
                                notiInfo = notiData.notiInfo.fromJson<NotiInfo>(),
                                onClickComplete = { type, date ->
                                    viewModel.isValidNotiDate(
                                        notiInfo = NotiInfo(
                                            notiType = type,
                                            notiDate = date,
                                        ),
                                        uploadType = WishItemUploadType.PARSING,
                                    )
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

    private fun sendSnackbarVisualChannel(snackbarVisuals: WishBoardSnackbarVisuals) {
        viewModel.sendSnackbarChannel(snackbarVisuals)
    }

    companion object {
        var wishBoardSnackbarHostState: SnackbarHostState = SnackbarHostState()
    }
}
