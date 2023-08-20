package com.hyeeyoung.wishboard.presentation.sign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBarWithStep
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.constant.WishBoardConstants
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.component.SignDescription

@Composable
fun SignUpPasswordScreen() {
    WishboardTheme {
        Scaffold(topBar = {
            WishBoardTopBarWithStep(
                topBarModel = WishBoardTopBarModel(title = stringResource(id = R.string.sign_up_title)),
                step = Pair(2, 2),
            )
        }) { paddingValues ->
            val emailInput = remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SignDescription(descriptionRes = R.string.sign_up_password_description, iconRes = R.drawable.ic_lock)

                WishBoardTextField(
                    input = emailInput,
                    placeholder = stringResource(id = R.string.sign_password_placeholder),
                    errorMsg = stringResource(id = R.string.sign_up_password_format_error), // TODO 기존 가입자 에러 메세지 추가
                    onTextChange = {},
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.weight(1f))

                TermsAndPolicyText()

                WishBoardWideButton(
                    enabled = false,
                    onClick = { /*TODO*/ },
                    text = stringResource(id = R.string.sign_up_title),
                )
            }
        }
    }
}

private const val TAG_TERMS = "terms"
private const val TAG_POLICY = "policy"

@Composable
fun TermsAndPolicyText() {
    val linkedSpanStyle = WishBoardTheme.typography.suitB4.run {
        SpanStyle(
            color = WishBoardTheme.colors.green700,
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            textDecoration = TextDecoration.Underline,
        )
    }

    val annotatedText = buildAnnotatedString {
        append("가입 시 ")

        pushStringAnnotation(
            tag = TAG_TERMS,
            annotation = WishBoardConstants.TERMS,
        )
        withStyle(
            style = linkedSpanStyle,
            block = { append("이용약관") },
        )
        pop()

        append(" 및 ")

        pushStringAnnotation(
            tag = TAG_POLICY,
            annotation = WishBoardConstants.PRIVACY_POLICY,
        )
        withStyle(
            style = linkedSpanStyle,
            block = { append("개인정보 처리방침") },
        )
        pop()

        append("에 동의하는 것으로 간주합니다.")
    }

    ClickableText(
        modifier = Modifier.padding(vertical = 6.dp),
        style = WishBoardTheme.typography.suitD3.copy(color = WishBoardTheme.colors.gray300),
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = TAG_TERMS,
                start = offset,
                end = offset,
            ).firstOrNull()?.let { annotation ->
                // TODO 웹뷰 이동, url은 annotation.item으로 접근
            }

            annotatedText.getStringAnnotations(
                tag = TAG_POLICY,
                start = offset,
                end = offset,
            ).firstOrNull()?.let { annotation ->
                // TODO 웹뷰 이동, url은 annotation.item으로 접근
            }
        },
    )
}

@Preview
@Composable
fun PreviewSignUpPasswordScreen() {
    SignUpPasswordScreen()
}
