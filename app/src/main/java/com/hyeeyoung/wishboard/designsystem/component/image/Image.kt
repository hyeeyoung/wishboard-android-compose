package com.hyeeyoung.wishboard.designsystem.component.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import com.hyeeyoung.wishboard.designsystem.style.BlackAlpha5
import com.hyeeyoung.wishboard.presentation.util.extension.coloredForeground

/** 전경색을 입힌 이미지를 로드 */
@Composable
fun Image(
    model: Any?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    alphaColor: Color = BlackAlpha5,
    placeHolder: @Composable (() -> Unit)? = null,
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .coloredForeground(alphaColor),
        model = model,
        contentScale = contentScale,
        contentDescription = contentDescription,
        loading = {
            placeHolder?.invoke()
        },
        error = {
            placeHolder?.invoke()
        }
    )
}
