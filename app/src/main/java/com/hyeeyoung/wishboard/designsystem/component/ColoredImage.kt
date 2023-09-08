package com.hyeeyoung.wishboard.designsystem.component

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.hyeeyoung.wishboard.designsystem.style.BlackAlpha5
import com.hyeeyoung.wishboard.presentation.util.extension.coloredForeground

/** 전경색을 입힌 이미지를 로드 */
@Composable
fun ColoredImage(
    model: Any?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    alphaColor: Color = BlackAlpha5,
) {
    Surface(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .coloredForeground(alphaColor),
            model = model,
            contentScale = contentScale,
            contentDescription = contentDescription,
        )
    }
}
