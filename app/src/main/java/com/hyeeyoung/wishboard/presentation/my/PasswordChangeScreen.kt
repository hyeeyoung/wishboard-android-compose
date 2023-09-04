package com.hyeeyoung.wishboard.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel

@Composable
fun PasswordChangeScreen(navController: NavHostController) {
    WishboardTheme {
        Scaffold(topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(
                    title = stringResource(id = R.string.my_password_change_title),
                    onClickStartIcon = { navController.popBackStack() },
                ),
            )
        }) { paddingValues ->
            val passwordInput = remember { mutableStateOf("") }
            val rePasswordInput = remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
            ) {
                Spacer(modifier = Modifier.size(32.dp))

                WishBoardTextField(
                    label = stringResource(id = R.string.my_password_new_password),
                    input = passwordInput,
                    placeholder = stringResource(id = R.string.my_password_new_password_placeholder),
                    errorMsg = stringResource(id = R.string.sign_up_password_format_error),
                    onTextChange = {},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.size(32.dp))

                WishBoardTextField(
                    label = stringResource(id = R.string.my_password_new_re_password),
                    input = rePasswordInput,
                    placeholder = stringResource(id = R.string.my_password_new_re_password_placeholder),
                    errorMsg = stringResource(id = R.string.my_password_incorrect_error),
                    onTextChange = {},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
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
fun PreviewPasswordChangeScreen() {
    PasswordChangeScreen(rememberNavController())
}
