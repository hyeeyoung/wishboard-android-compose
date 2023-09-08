package com.hyeeyoung.wishboard.designsystem.component.textfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardMiniSingleTextField(
    modifier: Modifier = Modifier,
    input: MutableState<String>,
    style: TextStyle = WishBoardTheme.typography.suitD2,
    placeholder: String,
    onTextChange: (String) -> Unit = {},
    maxLength: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val textFieldTextAlign = if (input.value.isEmpty()) TextAlign.Start else TextAlign.Center
    Column(modifier = modifier) {
        BasicTextField(
            value = input.value,
            onValueChange = {
                if (maxLength != null) { // TODO 붙여넣기 예외처리
                    if (it.length <= maxLength) input.value = it
                } else {
                    input.value = it
                }

                onTextChange(it)
            },
            textStyle = style.copy(color = WishBoardTheme.colors.gray700, textAlign = textFieldTextAlign),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
        ) { innerTextField ->
            Box() {
                if (input.value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = WishBoardTheme.colors.gray300,
                        style = style,
                    )
                }
                innerTextField()
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWishBoardMiniSingleTextField() {
    val input = remember { mutableStateOf("") }
    WishBoardMiniSingleTextField(
        input = input,
        placeholder = stringResource(id = R.string.wish_item_link_sharing_upload_name),
    )
}
