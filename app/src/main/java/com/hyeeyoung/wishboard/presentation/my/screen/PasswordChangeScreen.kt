package com.hyeeyoung.wishboard.presentation.my.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.presentation.my.model.MyUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack

@Composable
fun PasswordChangeScreen(navController: NavController, viewModel: MyViewModel = hiltViewModel()) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle() // TODO 리팩토링 필요

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    PasswordChangeScreen(
        uiModel = uiModel,
        onPasswordChange = { password, isRePassword ->
            viewModel.onPasswordChange(password = password, isRePassword = isRePassword)
        },
        onClickComplete = {
            viewModel.updatePassword {
                navController.safePopBackStack()
            }
        },
        onClickBack = navController::safePopBackStack,
    )
}

@Composable
fun PasswordChangeScreen(
    uiModel: MyUiModel,
    onPasswordChange: (password: String, isRePassword: Boolean) -> Unit,
    onClickComplete: () -> Unit,
    onClickBack: () -> Unit,
) {
    val isCorrectPassword by remember(uiModel.passwordInput, uiModel.rePasswordInput) {
        mutableStateOf(
            uiModel.passwordInput == uiModel.rePasswordInput
        )
    }

    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                title = stringResource(id = R.string.my_password_change_title),
                onClickStartIcon = onClickBack,
            ),
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            Spacer(modifier = Modifier.size(32.dp))

            WishBoardTextField(
                label = stringResource(id = R.string.my_password_new_password),
                input = uiModel.passwordInput,
                isError = uiModel.isValidPassword == false,
                placeholder = stringResource(id = R.string.my_password_new_password_placeholder),
                errorMsg = stringResource(id = R.string.sign_up_password_format_error),
                onTextChange = { onPasswordChange(it, false) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.size(32.dp))

            WishBoardTextField(
                label = stringResource(id = R.string.my_password_new_re_password),
                input = uiModel.rePasswordInput,
                isError = uiModel.rePasswordInput.isNotEmpty() && !isCorrectPassword,
                placeholder = stringResource(id = R.string.my_password_new_re_password_placeholder),
                errorMsg = stringResource(id = R.string.my_password_incorrect_error),
                onTextChange = { onPasswordChange(it, true) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.weight(1f))

            WishBoardWideButton(
                enabled = uiModel.isValidPassword == true && isCorrectPassword,
                onClick = onClickComplete,
                text = stringResource(id = R.string.complete),
            )
        }
    }
}

@Preview
@Composable
fun PreviewPasswordChangeScreen() {
    PasswordChangeScreen(rememberNavController())
}
