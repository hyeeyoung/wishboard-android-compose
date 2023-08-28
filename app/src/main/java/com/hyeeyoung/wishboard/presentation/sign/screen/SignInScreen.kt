package com.hyeeyoung.wishboard.presentation.sign.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun SignInScreen(
    onClickBack: () -> Unit = {},
    onClickLogin: () -> Unit = {},
    onClickForgotPassword: () -> Unit = {},
) {
    WishboardTheme {
        // TODO 화면 진입 시 키보드 올리기
//        val focusRequester = remember { FocusRequester() }
//        val keyboardController = LocalSoftwareKeyboardController.current

//        LaunchedEffect(Unit) {
//            focusRequester.requestFocus()
//        }

        Scaffold(topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(
                    title = stringResource(id = R.string.sign_in_title),
                    onClickStartIcon = onClickBack,
                ),
            )
        }) { paddingValues ->
            val emailInput = remember { mutableStateOf("") }
            val passwordInput = remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 8.dp, start = 16.dp, end = 16.dp),
            ) {
                Spacer(modifier = Modifier.size(32.dp))

                WishBoardTextField(
                    label = stringResource(id = R.string.sign_email),
                    input = emailInput,
                    placeholder = stringResource(id = R.string.sign_email_placeholder),
                    onTextChange = {},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                Spacer(modifier = Modifier.size(32.dp))

                WishBoardTextField(
                    label = stringResource(id = R.string.sign_password),
                    input = passwordInput,
                    placeholder = stringResource(id = R.string.sign_password_placeholder),
                    onTextChange = {},
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.weight(1f))

                WishBoardWideButton(
                    enabled = false,
                    onClick = { onClickLogin() },
                    text = stringResource(id = R.string.sign_in_title),
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                        .noRippleClickable { onClickForgotPassword() },
                    text = stringResource(id = R.string.sign_forget_password),
                    style = WishBoardTheme.typography.suitB3,
                    color = WishBoardTheme.colors.gray300,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignInScreen() {
    SignInScreen()
}
