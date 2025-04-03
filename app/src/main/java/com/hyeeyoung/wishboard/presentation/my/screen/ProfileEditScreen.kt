package com.hyeeyoung.wishboard.presentation.my.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.presentation.my.model.MyUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.createImageUri
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import kotlinx.coroutines.delay

@Composable
fun ProfileEditScreen(
    navController: NavController,
    viewModel: MyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    LaunchedEffect(Unit) {
        viewModel.fetchUserInfo()
        viewModel.setTokenForProfileImageUri()
    }

    ProfileEditScreen(
        uiModel = uiModel,
        context = context,
        onNicknameChange = viewModel::onNicknameChange,
        setImageUri = viewModel::setProfileImageUri,
        updateProfile = {
            viewModel.updateUserProfile(context,
                afterSuccess = {
                    navController.safePopBackStack()
                })
        },
        onClickBack = navController::safePopBackStack
    )
}

@Composable
fun ProfileEditScreen(
    uiModel: MyUiModel,
    context: Context,
    updateProfile: () -> Unit,
    setImageUri: (Uri?) -> Unit,
    onNicknameChange: (String) -> Unit,
    onClickBack: () -> Unit,
) {
    val imageSize = 106
    var cameraUri: Uri? = null
    val albumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { setImageUri(it) }
    }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) setImageUri(cameraUri)
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

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(300L)
        focusRequester.requestFocus()
    }

    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                title = stringResource(id = R.string.my_profile_edit_title),
                onClickStartIcon = onClickBack,
            ),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.size(32.dp))

            Box(
                modifier = Modifier
                    .width((imageSize + 12).dp)
                    .noRippleClickable { ModalData.OptionModal.ImageSelection.openModal(context, modalLauncher) },
            ) {
                Image(
                    model = uiModel.imageUriInput ?: uiModel.userInfo.profileImage,
                    modifier = Modifier
                        .size(imageSize.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape),
                    placeHolder = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_placeholder_user_profile),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }
                )

                Icon(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 5.dp),
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
            Spacer(modifier = Modifier.size(32.dp))
            WishBoardTextField(
                modifier = Modifier
                    .focusRequester(focusRequester),
                input = uiModel.nicknameInput,
                isError = uiModel.nicknameInput == uiModel.existingNickname,
                label = stringResource(id = R.string.my_profile_nickname),
                placeholder = stringResource(id = R.string.my_profile_nickname_placeholder),
                errorMsg = stringResource(id = R.string.my_profile_nickname_already_exist_error),
                maxLength = 12,
                onTextChange = onNicknameChange,
            )

            Spacer(modifier = Modifier.weight(1f))

            WishBoardWideButton(
                enabled = (uiModel.nicknameInput.isNotBlank() && uiModel.userInfo.nickname != uiModel.nicknameInput) || uiModel.imageUriInput != null,
                onClick = updateProfile,
                text = stringResource(id = R.string.complete),
            )
        }
    }
}

@Preview
@Composable
fun PreviewProfileEditScreen() {
    ProfileEditScreen(
        context = LocalContext.current,
        uiModel = MyUiModel(userInfo = UserInfo(nickname = "영진이")),
        setImageUri = {},
        onNicknameChange = {},
        updateProfile = {},
        onClickBack = {}
    )
}
