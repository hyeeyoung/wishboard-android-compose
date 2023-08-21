package com.hyeeyoung.wishboard.presentation.sign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBarWithStep
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.sign.component.SignDescription

@Composable
fun SignUpEmailScreen() {
    WishboardTheme {
        Scaffold(topBar = {
            WishBoardTopBarWithStep(
                topBarModel = WishBoardTopBarModel(title = stringResource(id = R.string.sign_up_title)),
                step = Pair(1, 2),
            )
        }) { paddingValues ->
            val emailInput = remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SignDescription(descriptionRes = R.string.sign_up_email_description, iconRes = R.drawable.ic_email)

                WishBoardTextField(
                    input = emailInput,
                    placeholder = stringResource(id = R.string.sign_email_placeholder),
                    errorMsg = stringResource(id = R.string.sign_in_email_error), // TODO 기존 가입자 에러 메세지 추가
                    onTextChange = {},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                Spacer(modifier = Modifier.weight(1f))

                WishBoardWideButton(
                    enabled = false,
                    onClick = { /*TODO*/ },
                    text = stringResource(id = R.string.next),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignUpEmailScreen() {
    SignUpEmailScreen()
}
