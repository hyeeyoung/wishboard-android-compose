package com.hyeeyoung.wishboard.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.dropUnlessResumed
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.model.WishBoardButtonColors
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.util.extension.rippleClickable

@Composable
fun WishBoardWideButton(
    modifier: Modifier = Modifier,
    state: WishBoardState<Unit> = WishBoardState.Idle,
    enabled: Boolean,
    onClick: () -> Unit,
    text: String,
    shape: Shape = RoundedCornerShape(12.dp),
    isGreen: Boolean = true,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_horizontal_black))

    val color = if (isGreen) {
        ButtonDefaults.buttonColors(
            containerColor = WishBoardTheme.colors.green500,
            disabledContainerColor = WishBoardTheme.colors.gray100,
            contentColor = WishBoardTheme.colors.gray700,
            disabledContentColor = WishBoardTheme.colors.gray300,
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = WishBoardTheme.colors.gray700,
            disabledContainerColor = WishBoardTheme.colors.gray100,
            contentColor = WishBoardTheme.colors.white,
            disabledContentColor = WishBoardTheme.colors.gray300,
        )
    }

    // 화면 전환하면서 화면 사라지는 중인데 클릭 이벤트를 받는 케이스가 있어 resumed 상태일 때만 클릭 이벤트를 받도록 함
    val safeClickIfResumed = dropUnlessResumed { onClick() }

    Box {
        if (state is WishBoardState.Loading) {
            LottieAnimation(
                modifier = Modifier
                    .height(48.dp)
                    .zIndex(2f)
                    .align(Alignment.Center),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
        }

        Button(
            modifier = modifier
                .fillMaxWidth(),
            onClick = { safeClickIfResumed() },
            shape = shape,
            enabled = enabled,
            colors = color,
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            Text(
                text = if (state != WishBoardState.Loading) text else "",
                style = WishBoardTheme.typography.suitH3,
            )
        }
    }
}

@Composable
fun WishBoardButton(
    modifier: Modifier = Modifier,
    color: WishBoardButtonColors = WishBoardButtonColors.GREEN,
    disabledColor: WishBoardButtonColors = WishBoardButtonColors.GRAY,
    state: WishBoardState<Unit> = WishBoardState.Idle,
    enabled: Boolean,
    onClick: () -> Unit,
    text: String,
    shape: Shape = RoundedCornerShape(12.dp),
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_horizontal_black))

    // 화면 전환하면서 화면 사라지는 중인데 클릭 이벤트를 받는 케이스가 있어 resumed 상태일 때만 클릭 이벤트를 받도록 함
    val safeClickIfResumed = dropUnlessResumed { onClick() }

    Box(modifier = modifier) {
        if (state is WishBoardState.Loading) {
            LottieAnimation(
                modifier = Modifier
                    .height(48.dp)
                    .zIndex(2f)
                    .align(Alignment.Center),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(if (enabled) color.backgroundColor else disabledColor.backgroundColor)
                .rippleClickable(enabled = enabled) {
                    safeClickIfResumed()
                }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (state != WishBoardState.Loading) text else "",
                style = WishBoardTheme.typography.suitH3,
                color = if (enabled) color.textColor else disabledColor.textColor,
            )
        }
    }
}

@Composable
fun WishBoardNarrowButton(modifier: Modifier = Modifier, enabled: Boolean, onClick: () -> Unit, text: String) {
    Button(
        modifier = modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(15.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = WishBoardTheme.colors.green500,
            disabledContainerColor = WishBoardTheme.colors.gray100,
            contentColor = WishBoardTheme.colors.gray700,
            disabledContentColor = WishBoardTheme.colors.gray300,
        ),
        contentPadding = PaddingValues(vertical = 7.dp, horizontal = 16.dp),
    ) {
        Text(
            text = text,
            style = WishBoardTheme.typography.suitB3,
        )
    }
}

@Composable
@Preview
fun PreviewWishBoardWideButton() {
    WishBoardWideButton(enabled = true, onClick = {}, text = stringResource(id = R.string.sign_in_title))
}

@Composable
@Preview
fun PreviewWishBoardButton() {
    WishBoardButton(enabled = true, onClick = {}, text = stringResource(id = R.string.sign_in_title))
}

@Composable
@Preview
fun PreviewWishBoardNarrowButton() {
    WishBoardNarrowButton(enabled = false, onClick = {}, text = stringResource(id = R.string.save))
}
