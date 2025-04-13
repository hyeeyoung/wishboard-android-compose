package com.hyeeyoung.wishboard.designsystem.component.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

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
            onClick = { onClick() },
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
@Preview
fun PreviewWishBoardWideButton() {
    WishBoardWideButton(enabled = true, onClick = {}, text = stringResource(id = R.string.sign_in_title))
}
