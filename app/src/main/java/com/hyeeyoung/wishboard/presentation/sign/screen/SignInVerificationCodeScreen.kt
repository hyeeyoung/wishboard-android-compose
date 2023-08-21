package com.hyeeyoung.wishboard.presentation.sign.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBarWithStep
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardTextFieldComponent
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.component.SignDescription

private const val VERIFICATION_CODE_MAX_LENGTH = 6

@Composable
fun SignInVerificationCodeScreen() {
    WishboardTheme {
        Scaffold(topBar = {
            WishBoardTopBarWithStep(
                topBarModel = WishBoardTopBarModel(title = stringResource(id = R.string.sign_in_email_title)),
                step = Pair(2, 2),
            )
        }) { paddingValues ->
            val verificationCodeInput = remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SignDescription(
                    descriptionRes = R.string.sign_in_verification_code_description,
                    iconRes = R.drawable.ic_lock,
                )

                WishBoardTextField(
                    input = verificationCodeInput,
                    placeholder = stringResource(id = R.string.sign_in_verification_code_placeholder),
                    errorMsg = stringResource(id = R.string.sign_in_verification_code_error),
                    onTextChange = { },
                    maxLength = VERIFICATION_CODE_MAX_LENGTH,
                    endComponent = WishBoardTextFieldComponent.Timer(4, 56), // TODO 실제 타이머에서 시간 데이터 가져오기
                )

                Spacer(modifier = Modifier.weight(1f))

                WishBoardWideButton(
                    enabled = false,
                    onClick = { /*TODO*/ },
                    text = stringResource(id = R.string.sign_in_title),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignInVerificationCodeScreen() {
    SignInVerificationCodeScreen()
}
