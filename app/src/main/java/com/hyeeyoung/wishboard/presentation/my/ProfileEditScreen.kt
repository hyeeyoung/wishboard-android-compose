package com.hyeeyoung.wishboard.presentation.my

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.dialog.ModalData
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.createImageUri
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher

@Composable
fun ProfileEditScreen(navController: NavHostController) {
    var imageInput by remember { mutableStateOf<Uri?>(null) }
    var cameraUri: Uri? = null
    val albumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            imageInput = it
        }
    }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) imageInput = cameraUri
        }

    val context = LocalContext.current
    val modalLauncher = rememberModalLauncher { isTopOption, data ->
        when (data) {
            is ModalData.OptionModal.ImageSelection -> {
                if (isTopOption) {
                    cameraUri = context.createImageUri("youngjin7wishboard") // TODO 실 토큰값 넣기
                    cameraLauncher.launch(cameraUri)
                } else {
                    albumLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }

            else -> {}
        }
    }

    WishboardTheme {
        Scaffold(topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(
                    title = stringResource(id = R.string.my_profile_edit_title),
                    onClickStartIcon = { navController.popBackStack() },
                ),
            )
        }) { paddingValues ->
            val nicknameInput = remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.size(32.dp))
                val imageSize = 106
                Box(
                    modifier = Modifier
                        .width((imageSize + 12).dp)
                        .noRippleClickable { ModalData.OptionModal.ImageSelection.openModal(context, modalLauncher) },
                ) {
                    if (imageInput != null) {
                        ColoredImage(
                            model = imageInput,
                            modifier = Modifier
                                .size(imageSize.dp)
                                .align(Alignment.Center).clip(CircleShape),
                        )
                    } else {
                        Icon(
                            modifier = Modifier
                                .size(imageSize.dp)
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.ic_placeholder_user_profile),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }

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
                    input = nicknameInput,
                    label = stringResource(id = R.string.my_profile_nickname),
                    placeholder = stringResource(id = R.string.my_profile_nickname_placeholder),
                    errorMsg = stringResource(id = R.string.my_profile_nickname_already_exist_error),
                    onTextChange = {},
                )

                Spacer(modifier = Modifier.weight(1f))

                WishBoardWideButton(
                    enabled = false,
                    onClick = { /*TODO*/ },
                    text = stringResource(id = R.string.complete),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewProfileEditScreen() {
    ProfileEditScreen(rememberNavController())
}
