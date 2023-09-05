package com.hyeeyoung.wishboard.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun ProfileEditScreen(navController: NavHostController) {
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
                        .noRippleClickable { /*TODO*/ },
                ) {
                    Icon(
                        modifier = Modifier
                            .size(imageSize.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_placeholder_user_profile),
                        contentDescription = null,
                        tint = Color.Unspecified,
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
                    input = nicknameInput,
                    label = stringResource(id = R.string.my_profile_nickname),
                    placeholder = stringResource(id = R.string.my_profile_nickname_placeholder),
                    errorMsg = stringResource(id = R.string.my_profile_nickname_already_exist_error),
                    onTextChange = {},
                )

                Spacer(modifier = Modifier.weight(1f))

                WishBoardWideButton(
                    enabled = false,
                    onClick = { navController.navigate(MainScreen.MyProfile.route) },
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
