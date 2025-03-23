package com.hyeeyoung.wishboard.presentation.upload.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.designsystem.component.LocalSnackbarHostState
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardSnackbarMessage
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.WishBoardSnackbarVisuals
import com.hyeeyoung.wishboard.presentation.upload.WishItemUploadViewModel
import com.hyeeyoung.wishboard.presentation.upload.model.UploadInputType
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@AndroidEntryPoint
class LinkSharingWishUploadActivity : ComponentActivity() {
    private val viewModel: WishItemUploadViewModel by viewModels()

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
                    createFolder = { name, afterSuccess -> viewModel.createFolder(name) {
                        afterSuccess()
                    }},
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
                    onClickClose = { finish() })
            }
        }
    }

    private fun sendSnackbarVisualChannel(snackbarVisuals: WishBoardSnackbarVisuals) {
        viewModel.sendSnackbarChannel(snackbarVisuals)
    }

    companion object {
        var wishBoardSnackbarHostState: SnackbarHostState = SnackbarHostState()
    }
}
