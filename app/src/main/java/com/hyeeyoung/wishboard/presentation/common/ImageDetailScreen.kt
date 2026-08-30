package com.hyeeyoung.wishboard.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.onboarding.WishBoardIndicator

@Composable
fun ImageDetailScreen(
    images: List<String>,
    initialIndex: Int = 0,
    onClickClose: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { images.size })
    val zoomedPages: SnapshotStateList<Boolean> = remember(images.size) {
        List(images.size) { false }.toMutableStateList()
    }
    val isPagerScrollEnabled = !zoomedPages.getOrElse(pagerState.currentPage) { false }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 9.dp, end = 10.dp)
                .zIndex(2f)
                .align(Alignment.TopEnd)
                .background(color = WishBoardTheme.colors.black.copy(alpha = 0.1f), shape = CircleShape),
        ) {
            WishBoardIconButton(
                size = 36.dp,
                iconRes = R.drawable.ic_close,
                tint = WishBoardTheme.colors.white,
                contentDescription = "닫기 버튼",
                onClick = {
                    onClickClose()
                },
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = isPagerScrollEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                ZoomableImage(
                    url = images[page],
                    onZoomChanged = { isZoomed -> zoomedPages[page] = isZoomed },
                )
            }

            if (images.size > 1) {
                WishBoardIndicator(
                    modifier = Modifier.padding(bottom = 36.dp),
                    size = images.size,
                    pagerState = pagerState,
                    inactiveColor = WishBoardTheme.colors.gray300,
                    activeColor = WishBoardTheme.colors.white,
                )
            }
        }
    }
}

@Composable
private fun ZoomableImage(url: String, onZoomChanged: (Boolean) -> Unit = {}) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 5f)
        // pan은 이전 scale이 1f 초과일 때만 적용 — 스와이프 제스처의 pan 이벤트가 offset을 오염시키는 것을 막음
        if (scale > 1f) offset += panChange
        scale = newScale
        if (newScale == 1f) offset = Offset.Zero
        onZoomChanged(newScale > 1f)
    }

    Image(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y,
            )
            .transformable(state = transformableState, canPan = { scale > 1f })
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = 1f
                        offset = Offset.Zero
                        onZoomChanged(false)
                    },
                )
            },
        contentScale = ContentScale.Fit,
        alphaColor = Color.Transparent,
    )
}

@Preview
@Composable
fun PreviewImageDetailScreen() {
    ImageDetailScreen(
        images = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
        initialIndex = 0,
        onClickClose = {},
    )
}
