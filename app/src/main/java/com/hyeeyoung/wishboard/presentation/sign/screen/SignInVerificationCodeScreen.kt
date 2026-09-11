package com.hyeeyoung.wishboard.presentation.sign.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBarWithStep
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.common.getSharedViewModel
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import com.hyeeyoung.wishboard.presentation.sign.component.SignDescription
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTextFieldComponent
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.model.auth.SignUiModel
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import kotlinx.coroutines.delay

private const val VERIFICATION_CODE_MAX_LENGTH = 6

@Composable
fun SignInVerificationCodeScreen(
    navController: NavController,
    viewModel: SignViewModel = getSharedViewModel(
        navController = navController,
        route = SignScreen.Email.route,
    ),
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(true) {
        viewModel.startTimer()
    }

    SignInVerificationCodeScreen(
        uiModel = uiModel,
        onAuthCodeChange = viewModel::onAuthCodeChange,
        onClickLogin = {
            viewModel.signInEmail {
                keyboardController?.hide()
                navController.navigate("${MainScreen.Root.route}/${false}") {
                    popUpTo(route = SignScreen.Root.route) {
                        inclusive = true
                    }
                }
            }
        },
        onClickBack = {
            navController.safePopBackStack()
        },
    )
}

@Composable
fun SignInVerificationCodeScreen(
    uiModel: SignUiModel,
    onAuthCodeChange: (String) -> Unit,
    onClickLogin: () -> Unit,
    onClickBack: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        delay(300L)
        focusRequester.requestFocus()
    }

    Scaffold(topBar = {
        WishBoardTopBarWithStep(
            topBarModel = WishBoardTopBarModel(
                onClickStartIcon = onClickBack,
            ),
            step = Pair(2, 2),
        )
    }) { paddingValues ->
        val verificationCodeInput = remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WishBoardTheme.colors.white)
                .verticalScroll(rememberScrollState())
                .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp)
                .imePadding(),
        ) {
            SignDescription(
                titleRes = R.string.sign_in_verification_code_title,
                descriptionRes = R.string.sign_in_verification_code_description,
            )

            WishBoardTextField(
                modifier = Modifier.focusRequester(focusRequester),
                input = verificationCodeInput,
                placeholder = stringResource(id = R.string.sign_in_verification_code_placeholder),
                errorMsg = when {
                    uiModel.isCorrectAuthCode == false -> stringResource(id = R.string.sign_in_verification_code_error)
                    uiModel.isNonRegisteredUser -> stringResource(id = R.string.sign_in_unregister_error)
                    else -> ""
                },
                onTextChange = onAuthCodeChange,
                maxLength = VERIFICATION_CODE_MAX_LENGTH,
                isError = uiModel.isCorrectAuthCode == false || uiModel.isNonRegisteredUser,
                endComponent = WishBoardTextFieldComponent.Timer(uiModel.timer),
            )

            Spacer(modifier = Modifier.weight(1f))

            WishBoardWideButton(
                enabled = uiModel.authCode.length == VERIFICATION_CODE_MAX_LENGTH,
                state = uiModel.checkVerificationCodeStatus,
                onClick = onClickLogin,
                text = stringResource(id = R.string.sign_in_title),
            )
        }
    }
}

@Preview
@Composable
fun PreviewSignInVerificationCodeScreen() {
    SignInVerificationCodeScreen(
        uiModel = SignUiModel(timer = "5:00"),
        onAuthCodeChange = {},
        onClickLogin = {},
        onClickBack = {},
    )
}
