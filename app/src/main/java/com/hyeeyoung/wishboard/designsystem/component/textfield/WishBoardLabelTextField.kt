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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardString
import com.hyeeyoung.wishboard.presentation.util.buildStringWithSpans

@Composable
fun WishBoardLabelTextField(
    modifier: Modifier = Modifier,
    textFieldValue: TextFieldValue,
    label: List<WishBoardString>,
    spanStyle: SpanStyle,
    placeholder: String,
    onTextChange: (TextFieldValue) -> Unit = {},
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column {
        TextFieldLabel(modifier = modifier, label = label, spanStyle = spanStyle)

        BasicTextField(
            modifier = modifier
                .padding(top = 14.dp)
                .onFocusChanged { isFocused = it.isFocused },
            value = textFieldValue,
            onValueChange = { s ->
                if (s.text.length <= maxLength) {
                    onTextChange(s)
                }
            },
            textStyle = WishBoardTheme.typography.suitD1.copy(color = WishBoardTheme.colors.gray700),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
        ) { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = WishBoardTheme.colors.gray200,
                            style = WishBoardTheme.typography.suitD1,
                        )
                    }

                    innerTextField()
                }
            }
        }
    }
}

@Composable
private fun TextFieldLabel(
    modifier: Modifier = Modifier,
    label: List<WishBoardString>,
    spanStyle: SpanStyle,
) {
    Text(
        modifier = modifier,
        text = buildStringWithSpans(spanStrings = label, spanStyle = spanStyle),
        color = WishBoardTheme.colors.gray700,
        style = WishBoardTheme.typography.suitB2,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWishBoardLabelTextField() {
    WishBoardLabelTextField(
        modifier = Modifier.fillMaxWidth(),
        label = listOf(
            WishBoardString.NormalString("상품명 "),
            WishBoardString.SpanString("*"),
        ),
        spanStyle = WishBoardTheme.typography.suitB2.copy(color = WishBoardTheme.colors.green700).toSpanStyle(),
        textFieldValue = TextFieldValue(""),
        placeholder = "상품명을 입력해 주세요.",
    )
}
