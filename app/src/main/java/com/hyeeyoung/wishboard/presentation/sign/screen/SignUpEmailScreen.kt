package com.hyeeyoung.wishboard.presentation.sign.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBarWithStep
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import com.hyeeyoung.wishboard.presentation.sign.component.SignDescription
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.model.auth.SignUiModel
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import kotlinx.coroutines.delay

@Composable
fun SignUpEmailScreen(
    navController: NavHostController,
    viewModel: SignViewModel = hiltViewModel()
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    SignUpEmailScreen(
        uiModel = uiModel,
        onEmailChange = viewModel::onEmailChange,
        onClickNext = {
            viewModel.checkRegisteredUser(
                afterSuccess = {
                    keyboardController?.hide()
                    navController.navigate(SignScreen.Password.route)
                })
        },
        onClickBack = {
            keyboardController?.hide()
            navController.safePopBackStack()
        }
    )
}

@Composable
fun SignUpEmailScreen(
    uiModel: SignUiModel,
    onEmailChange: (String) -> Unit,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val isRegisteredEmail by remember(uiModel.email, uiModel.registeredEmail) {
        mutableStateOf(uiModel.email == uiModel.registeredEmail)
    }

    LaunchedEffect(Unit) {
        delay(300L)
        focusRequester.requestFocus()
    }

    Scaffold(topBar = {
        WishBoardTopBarWithStep(
            topBarModel = WishBoardTopBarModel(
                title = stringResource(id = R.string.sign_up_title),
                onClickStartIcon = { onClickBack() },
            ),
            step = Pair(1, 2),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SignDescription(descriptionRes = R.string.sign_up_email_description, iconRes = R.drawable.ic_email)

            WishBoardTextField(
                modifier = Modifier
                    .focusRequester(focusRequester),
                input = uiModel.email,
                placeholder = stringResource(id = R.string.sign_email_placeholder),
                errorMsg = if (uiModel.isValidEmail == false) stringResource(id = R.string.sign_in_email_error) else stringResource(
                    id = R.string.sign_up_already_member_error
                ),
                isError = uiModel.isValidEmail == false || isRegisteredEmail,
                onTextChange = onEmailChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.weight(1f))

            WishBoardWideButton(
                enabled = uiModel.isValidEmail == true && !isRegisteredEmail,
                onClick = onClickNext,
                text = stringResource(id = R.string.next),
            )
        }
    }
}

@Preview
@Composable
fun PreviewSignUpEmailScreen() {
    SignUpEmailScreen(
        uiModel = SignUiModel(
            email = "",
            registeredEmail = "cyjin6@naver.com",
            isValidEmail = null
        ),
        onEmailChange = {},
        onClickNext = {},
        onClickBack = {}
    )
}
