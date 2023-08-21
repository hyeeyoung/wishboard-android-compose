package com.hyeeyoung.wishboard.designsystem.component.text

import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.LinkedString

@Composable
fun WishBoardClickableText(
    modifier: Modifier = Modifier,
    style: TextStyle = WishBoardTheme.typography.suitD3.copy(color = WishBoardTheme.colors.gray300),
    linkedStrings: List<LinkedString>,
    spanStyle: SpanStyle,
    onClick: (Uri) -> Unit,
) {
    val annotatedText = buildAnnotatedString {
        linkedStrings.forEach { linkedStr ->
            if (linkedStr.linkInfo == null) {
                append(linkedStr.str)
            } else {
                pushStringAnnotation(
                    tag = linkedStr.linkInfo.tag,
                    annotation = linkedStr.linkInfo.link,
                )
                withStyle(style = spanStyle) { append(linkedStr.str) }
                pop()
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
                onClick(Uri.parse(link.item))
            }
        },
    )
}