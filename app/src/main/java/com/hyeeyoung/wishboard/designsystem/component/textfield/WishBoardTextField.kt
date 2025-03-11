package com.hyeeyoung.wishboard.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.WishBoardTextFieldComponent

@Composable
fun WishBoardTextField(
    modifier: Modifier = Modifier,
    input: String,
    label: String? = null,
    errorMsg: String? = null,
    placeholder: String,
    onTextChange: (String) -> Unit = {},
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    endComponent: WishBoardTextFieldComponent = WishBoardTextFieldComponent.DeleteButton,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column {
        TextFieldLabel(label = label)

        BasicTextField(
            modifier = modifier
                .onFocusChanged { isFocused = it.isFocused },
            value = input,
            onValueChange = { s ->
                if (s.length <= maxLength) {
                    onTextChange(s)
                }
            },
            textStyle = WishBoardTheme.typography.suitD1.copy(color = WishBoardTheme.colors.gray700),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
        ) { innerTextField ->
            DecorationBox(
                isFocused = isFocused,
                input = input,
                placeholder = placeholder,
                endComponent = endComponent,
                innerTextField = innerTextField,
                onClickClear = {
                    onTextChange("")
                }
            )
        }

        if (isFocused) {
            TextFieldErrorMessage(isError = isError, errorMsg = errorMsg)
        }
    }
}

@Composable
fun WishBoardTextField(
    modifier: Modifier = Modifier,
    input: MutableState<String>,
    label: String? = null,
    errorMsg: String? = null,
    placeholder: String,
    onTextChange: (String) -> Unit = {},
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    endComponent: WishBoardTextFieldComponent = WishBoardTextFieldComponent.DeleteButton,
) {
    var isFocused by remember { mutableStateOf(false) }
    Column {
        TextFieldLabel(label = label)

        BasicTextField(
            modifier = modifier
                .onFocusChanged { isFocused = it.isFocused },
            value = input.value,
            onValueChange = {
                if (it.length <= maxLength) {
                    input.value = it
                    onTextChange(it)
                }
            },
            textStyle = WishBoardTheme.typography.suitD1.copy(color = WishBoardTheme.colors.gray700),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
        ) { innerTextField ->
            DecorationBox(
                isFocused = isFocused,
                input = input.value,
                placeholder = placeholder,
                endComponent = endComponent,
                innerTextField = innerTextField,
                onClickClear = {
                    input.value = ""
                }
            )
        }

        if (isFocused) {
            TextFieldErrorMessage(isError = isError, errorMsg = errorMsg)
        }
    }
}

@Composable
private fun TextFieldLabel(
    label: String?,
) {
    if (label == null) return
    Column {
        Text(
            text = label,
            color = WishBoardTheme.colors.gray700,
            style = WishBoardTheme.typography.suitB3,
        )
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Composable
private fun TextFieldErrorMessage(isError: Boolean, errorMsg: String?) {
    if (!isError || errorMsg == null) return
    Spacer(modifier = Modifier.size(6.dp))
    Text(
        text = errorMsg,
        color = WishBoardTheme.colors.pink700,
        style = WishBoardTheme.typography.suitD3,
    )
}

@Composable
private fun DecorationBox(
    isFocused: Boolean,
    input: String,
    placeholder: String,
    endComponent: WishBoardTextFieldComponent = WishBoardTextFieldComponent.DeleteButton,
    innerTextField: @Composable () -> Unit,
    onClickClear: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(
                color = WishBoardTheme.colors.gray50,
                shape = RoundedCornerShape(6.dp),
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
                .padding(start = 10.dp)
        ) {
            if (input.isEmpty()) {
                Text(
                    text = placeholder,
                    color = WishBoardTheme.colors.gray300,
                    style = WishBoardTheme.typography.suitD1,
                )
            }

            innerTextField()
        }

        if (isFocused) {
            when (endComponent) {
                is WishBoardTextFieldComponent.DeleteButton -> {
                    if (input.isNotEmpty()) {
                        Spacer(modifier = Modifier.size(2.dp))
                        WishBoardIconButton(iconRes = R.drawable.ic_delete_circle, onClick = onClickClear)
                    } else {
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }

                is WishBoardTextFieldComponent.Timer -> {
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = endComponent.time,
                        color = WishBoardTheme.colors.pink700,
                        style = WishBoardTheme.typography.suitD2,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWishBoardTextField() {
    val input = remember { mutableStateOf("") }
    WishBoardTextField(
        modifier = Modifier.fillMaxWidth(),
        input = input,
        placeholder = stringResource(id = R.string.sign_email_placeholder),
        label = stringResource(id = R.string.sign_email),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWishBoardTextFieldWithTimer() {
    val input = remember { mutableStateOf("") }
    WishBoardTextField(
        modifier = Modifier.fillMaxWidth(),
        input = input,
        placeholder = stringResource(id = R.string.sign_in_verification_code_placeholder),
        endComponent = WishBoardTextFieldComponent.Timer("5:00"),
    )
}
