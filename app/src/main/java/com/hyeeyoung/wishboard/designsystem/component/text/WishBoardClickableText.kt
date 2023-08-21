package com.hyeeyoung.wishboard.designsystem.component.text

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.hyeeyoung.wishboard.presentation.model.WishBoardString

@Composable
fun WishBoardClickableText(
    modifier: Modifier = Modifier,
    style: TextStyle,
    strings: List<WishBoardString>,
    spanStyle: SpanStyle,
    onClick: (String) -> Unit,
) {
    val annotatedText = buildAnnotatedString {
        strings.forEach { str ->
            when (str) {
                is WishBoardString.NormalString -> {
                    append(str.value)
                }

                is WishBoardString.LinkedString -> {
                    pushStringAnnotation(
                        tag = str.tag,
                        annotation = str.link,
                    )
                    withStyle(style = spanStyle) { append(str.value) }
                    pop()
                }

                else -> {
                    throw IllegalStateException("이 외 타입은 append 불가")
                }
            }
        }
    }

    ClickableText(
        modifier = modifier,
        style = style,
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                start = offset,
                end = offset,
            ).firstOrNull()?.let { link ->
                onClick(link.item)
            }
        },
    )
}
