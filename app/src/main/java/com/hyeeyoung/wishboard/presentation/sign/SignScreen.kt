package com.hyeeyoung.wishboard.presentation.sign

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.util.buildStringWithSpans
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.SpanString
import com.hyeeyoung.wishboard.util.extension.noRippleClickable

@Composable
fun SignScreen() {
    WishboardTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 24.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_welcome),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )

                    Spacer(modifier = Modifier.size(24.dp))

                    Image(
                        modifier = Modifier.height(24.dp),
                        painter = painterResource(id = R.drawable.ic_app_text_logo),
                        contentDescription = null,
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Text(
                        text = "흩어져있는 위시리스트를\n" +
                            "위시보드로 간편하게 통합 관리해 보세요!️",
                        style = WishBoardTheme.typography.suitD2M,
                        color = WishBoardTheme.colors.gray700,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                WishBoardWideButton(
                    enabled = true,
                    onClick = { /*TODO*/ },
                    text = stringResource(id = R.string.sign_up_title),
                )

                Spacer(modifier = Modifier.size(8.dp))

                val linkedSpanStyle = WishBoardTheme.typography.suitH4.run {
                    SpanStyle(
                        color = WishBoardTheme.colors.green700,
                        fontSize = fontSize,
                        fontFamily = fontFamily,
                        fontWeight = fontWeight,
                        textDecoration = TextDecoration.Underline,
                    )
                }

                Text(
                    modifier = Modifier
                        .noRippleClickable { /** TODO */ }
                        .padding(8.dp),
                    text = buildStringWithSpans(
                        spanStrings = listOf(
                            SpanString.NormalString("이미 계정이 있으신가요? "),
                            SpanString.Span("로그인"),
                        ),
                        spanStyle = linkedSpanStyle,
                    ),
                    style = WishBoardTheme.typography.suitD2,
                    color = WishBoardTheme.colors.gray300,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignScreen() {
    SignScreen()
}
