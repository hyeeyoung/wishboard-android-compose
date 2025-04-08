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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.text.WishBoardClickableText
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBarWithStep
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.common.getSharedViewModel
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import com.hyeeyoung.wishboard.presentation.sign.component.SignDescription
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardString
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.model.auth.SignUiModel
import com.hyeeyoung.wishboard.presentation.util.constant.WishBoardUrl
import com.hyeeyoung.wishboard.presentation.util.extension.moveToWebView
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import kotlinx.coroutines.delay

@Composable
fun SignUpPasswordScreen(
    navController: NavHostController,
    viewModel: SignViewModel = getSharedViewModel<SignViewModel>(navController, SignScreen.Email.route)
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    SignUpPasswordScreen(
        uiModel = uiModel,
        onPasswordChange = viewModel::onPasswordChange,
        onClickTermsOrPolicy = { url, title ->
            navController.moveToWebView(
                title = title,
                url = url,
            )
        },
        onClickSignUp = {
            viewModel.signUp(afterSuccess = {
                keyboardController?.hide()
                navController.navigate("${MainScreen.Root.route}/${true}") {
                    popUpTo(route = SignScreen.Root.route) {
                        inclusive = true
                    }
                }
            })
        },
        onClickBack = {
            navController.safePopBackStack()
        }
    )
}

@Composable
fun SignUpPasswordScreen(
    uiModel: SignUiModel,
    onPasswordChange: (String) -> Unit,
    onClickTermsOrPolicy: (url: String, title: String) -> Unit,
    onClickSignUp: () -> Unit,
    onClickBack: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(300L)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(topBar = {
        WishBoardTopBarWithStep(
            topBarModel = WishBoardTopBarModel(
                title = stringResource(id = R.string.sign_up_title),
                onClickStartIcon = onClickBack,
            ),
            step = Pair(2, 2),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SignDescription(descriptionRes = R.string.sign_up_password_description, iconRes = R.drawable.ic_lock)

            WishBoardTextField(
                modifier = Modifier
                    .focusRequester(focusRequester),
                input = uiModel.password,
                placeholder = stringResource(id = R.string.sign_password_placeholder),
                errorMsg = when {
                    uiModel.isValidPassword == false -> stringResource(id = R.string.sign_up_password_format_error)
                    uiModel.isAlreadyRegisteredError -> stringResource(id = R.string.sign_up_already_member_error)
                    else -> ""
                },
                onTextChange = onPasswordChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isError = uiModel.isValidPassword == false || uiModel.isAlreadyRegisteredError
            )

            Spacer(modifier = Modifier.weight(1f))

            TermsAndPolicyText(onClickTermsOrPolicy = { url, title ->
                onClickTermsOrPolicy(url, title)
            })

            WishBoardWideButton(
                enabled = uiModel.isValidPassword == true,
                onClick = onClickSignUp,
                text = stringResource(id = R.string.sign_up_title),
            )
        }
    }
}

@Composable
fun TermsAndPolicyText(onClickTermsOrPolicy: (String, String) -> Unit) {
    val linkedSpanStyle = WishBoardTheme.typography.suitB4.run {
        SpanStyle(
            color = WishBoardTheme.colors.green700,
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            textDecoration = TextDecoration.Underline,
        )
    }

    val linkedStrings = listOf(
        WishBoardString.NormalString(value = "가입 시 "),
        WishBoardString.LinkedString(
            value = WishBoardUrl.TERMS.title,
            tag = WishBoardUrl.TERMS.name,
            link = WishBoardUrl.TERMS.url,
        ),
        WishBoardString.NormalString(value = " 및 "),
        WishBoardString.LinkedString(
            value = WishBoardUrl.PRIVACY_POLICY.title,
            tag = WishBoardUrl.PRIVACY_POLICY.name,
            link = WishBoardUrl.PRIVACY_POLICY.url,
        ),
        WishBoardString.NormalString(value = "에 동의하는 것으로 간주합니다."),
    )

    WishBoardClickableText(
        modifier = Modifier.padding(vertical = 6.dp),
        style = WishBoardTheme.typography.suitD3.copy(color = WishBoardTheme.colors.gray300),
        strings = linkedStrings,
        spanStyle = linkedSpanStyle,
        onClick = { link ->
            val title =
                when (link) {
                    WishBoardUrl.TERMS.url -> WishBoardUrl.TERMS.title
                    WishBoardUrl.PRIVACY_POLICY.url -> WishBoardUrl.PRIVACY_POLICY.title
                    else -> throw IllegalStateException("유효하지 않은 링크")
                }
            onClickTermsOrPolicy(link, title)
        },
    )
}

@Preview
@Composable
fun PreviewSignUpPasswordScreen() {
    SignUpPasswordScreen(
        uiModel = SignUiModel(),
        onClickTermsOrPolicy = { _, _ -> },
        onPasswordChange = {},
        onClickSignUp = {},
        onClickBack = {}
    )
}
