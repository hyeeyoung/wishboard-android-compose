package com.hyeeyoung.wishboard.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    input: MutableState<String>,
    label: String? = null,
    errorMsg: String? = null,
    placeholder: String,
    onTextChange: (String) -> Unit,
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLength: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    endComponent: WishBoardTextFieldComponent = WishBoardTextFieldComponent.DeleteButton,
) {
    Column() {
        // 라벨
        if (label != null) {
            Text(
                text = label,
                color = WishBoardTheme.colors.gray700,
                style = WishBoardTheme.typography.suitB3,
            )
            Spacer(modifier = Modifier.size(8.dp))
        }

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
            textStyle = WishBoardTheme.typography.suitD1.copy(color = WishBoardTheme.colors.gray700),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
        ) { innerTextField ->
            Row(
                modifier = modifier
                    .height(42.dp)
                    .background(
                        color = WishBoardTheme.colors.gray50,
                        shape = RoundedCornerShape(6.dp),
                    )
                    .padding(start = 10.dp, end = 2.dp), // end 변경 시 end 위치에 있는 컴포저블과 화면 가장자리 간 여백 조절 필요
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (input.value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = WishBoardTheme.colors.gray300,
                            style = WishBoardTheme.typography.suitD1,
                        )
                    }
                    innerTextField()
                }

                when (endComponent) {
                    is WishBoardTextFieldComponent.DeleteButton -> {
                        if (input.value.isNotEmpty()) {
                            Spacer(modifier = Modifier.size(2.dp))
                            WishBoardIconButton(iconRes = R.drawable.ic_delete_circle, onClick = { input.value = "" })
                        } else {
                            Spacer(modifier = Modifier.size(8.dp))
                        }
                    }

                    is WishBoardTextFieldComponent.Timer -> {
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = stringResource(
                                id = R.string.timer_format,
                                formatArgs = arrayOf(endComponent.minute, endComponent.second),
                            ),
                            color = WishBoardTheme.colors.pink700,
                            style = WishBoardTheme.typography.suitD2,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }

        // 에러 메세지
        if (isError && errorMsg != null) {
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = errorMsg,
                color = WishBoardTheme.colors.pink700,
                style = WishBoardTheme.typography.suitD3,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWishBoardBasicTextField() {
    val input = remember { mutableStateOf("") }
    WishBoardTextField(
        modifier = Modifier.fillMaxWidth(),
        input = input,
        placeholder = stringResource(id = R.string.sign_email_placeholder),
        onTextChange = {},
        label = stringResource(id = R.string.sign_email),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWishBoardBasicTextFieldWithTimer() {
    val input = remember { mutableStateOf("") }
    WishBoardTextField(
        modifier = Modifier.fillMaxWidth(),
        input = input,
        placeholder = stringResource(id = R.string.sign_in_verification_code_placeholder),
        onTextChange = {},
        endComponent = WishBoardTextFieldComponent.Timer(4, 56),
    )
}
