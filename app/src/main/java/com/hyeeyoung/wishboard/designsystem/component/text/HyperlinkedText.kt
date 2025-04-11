package com.hyeeyoung.wishboard.designsystem.component.text

import android.util.Patterns
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.getDomainName
import com.hyeeyoung.wishboard.presentation.util.extension.moveToWebView

private const val GROUP_NAME_TAG = "url"

@Composable
fun HyperlinkText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color,
    navController: NavController,
) {
    val annotatedText = buildAnnotatedString {
        var currentIndex = 0
        val matcher = Patterns.WEB_URL.matcher(text)

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            // 일반 텍스트 추가
            if (start > currentIndex) {
                append(text.substring(currentIndex, start))
            }

            val url = text.substring(start, end)
            append(url)

            addLink(
                LinkAnnotation.Clickable(url, styles = TextLinkStyles(
                    style = SpanStyle(color = Color(0xFF3776E7))
                ), linkInteractionListener = {
                    navController.moveToWebView(title = url.getDomainName(), url = url)
                }),
                start = length - url.length,
                end = length
            )

            currentIndex = end
        }

        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }

    BasicText(
        modifier = modifier,
        text = annotatedText,
        style = style,
        color = { color },
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewHyperlinkText() {
    HyperlinkText(
        navController = rememberNavController(),
        style = WishBoardTheme.typography.suitD1,
        color = WishBoardTheme.colors.gray700,
        text = "네이버 링크는 https://www.naver.com 입니다."
    )
}
