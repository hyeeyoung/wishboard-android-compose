package com.hyeeyoung.wishboard.designsystem.util

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.hyeeyoung.wishboard.presentation.model.WishBoardString

fun buildStringWithSpans(
    spanStrings: List<WishBoardString>,
    spanStyle: SpanStyle,
) = buildAnnotatedString {
    spanStrings.forEach { spanStr ->
        when (spanStr) {
            is WishBoardString.NormalString -> append(spanStr.value)
            is WishBoardString.SpanString -> withStyle(style = spanStyle) { append(spanStr.value) }
            else -> {
                throw IllegalStateException("이 외 타입은 append 불가")
            }
        }
    }
}
