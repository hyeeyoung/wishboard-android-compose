package com.hyeeyoung.wishboard.designsystem.component.textfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardSimpleTextField(
    modifier: Modifier = Modifier,
    input: MutableState<String>,
    placeholder: String,
    onTextChange: (String) -> Unit = {},
    singleLine: Boolean = true,
    maxLength: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column {
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
            textStyle = WishBoardTheme.typography.suitB3.copy(color = WishBoardTheme.colors.gray700),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
        ) { innerTextField ->
            Row(
                modifier = modifier
                    .padding(16.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (input.value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = WishBoardTheme.colors.gray200,
                            style = WishBoardTheme.typography.suitB3,
                        )
                    }
                    innerTextField()
                }
            }
        }
        WishBoardDivider()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWishBoardSimpleTextField() {
    val input = remember { mutableStateOf("") }
    WishBoardSimpleTextField(
        modifier = Modifier.fillMaxWidth(),
        input = input,
        placeholder = stringResource(id = R.string.wish_item_upload_item_name),
    )
}
