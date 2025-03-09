package com.hyeeyoung.wishboard.presentation.sign.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.designsystem.component.PlusGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.model.auth.SignUiModel
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import kotlinx.coroutines.delay

@Composable
fun SignInScreen(navController: NavHostController, viewModel: SignViewModel = hiltViewModel()) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()

    PlusGlobalSnackbarMessage(viewModel.snackBarChannel)

    SignInScreen(
        uiModel = uiModel,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onClickLogin = {
            viewModel.signIn(
                afterSuccess = {
                    navController.navigate(MainScreen.Root.route) {
                        popUpTo(route = SignScreen.Root.route) {
                            inclusive = true
                        }
                    }
                }
            )
        },
        onClickEmailLogin = {
            navController.navigate(SignScreen.EmailLogin.route)
        },
        onClickBack = navController::popBackStack
    )
}

@Composable
fun SignInScreen(
    uiModel: SignUiModel,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onClickLogin: () -> Unit,
    onClickEmailLogin: () -> Unit,
    onClickBack: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(300L)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                title = stringResource(id = R.string.sign_in_title),
                onClickStartIcon = onClickBack,
            ),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding(), bottom = 8.dp, start = 16.dp, end = 16.dp),
        ) {
            Spacer(modifier = Modifier.size(32.dp))

            WishBoardTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .focusable(),
                label = stringResource(id = R.string.sign_email),
                input = uiModel.email,
                placeholder = stringResource(id = R.string.sign_email_placeholder),
                onTextChange = onEmailChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.size(32.dp))

            WishBoardTextField(
                label = stringResource(id = R.string.sign_password),
                input = uiModel.password,
                placeholder = stringResource(id = R.string.sign_password_placeholder),
                onTextChange = onPasswordChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.weight(1f))

            WishBoardWideButton(
                enabled = uiModel.isValidEmail == true && uiModel.isValidPassword == true,
                onClick = onClickLogin,
                text = stringResource(id = R.string.sign_in_title),
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .noRippleClickable { onClickEmailLogin() },
                text = stringResource(id = R.string.sign_forget_password),
                style = WishBoardTheme.typography.suitB3,
                color = WishBoardTheme.colors.gray300,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}

@Preview
@Composable
fun PreviewSignInScreen() {
    SignInScreen(
        uiModel = SignUiModel(),
        onEmailChange = {},
        onPasswordChange = {},
        onClickLogin = {},
        onClickEmailLogin = {},
        onClickBack = {}
    )
}
