package com.hyeeyoung.wishboard.designsystem.util

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.hyeeyoung.wishboard.presentation.model.SpanString

fun buildStringWithSpans(
    spanStrings: List<SpanString>,
    spanStyle: SpanStyle,
) = buildAnnotatedString {
    spanStrings.forEach { spanStr ->
        when (spanStr) {
            is SpanString.NormalString -> append(spanStr.value)
            is SpanString.Span -> withStyle(style = spanStyle) { append(spanStr.value) }
        }
    }
}
