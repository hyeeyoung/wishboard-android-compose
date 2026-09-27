package com.hyeeyoung.wishboard.designsystem.component.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.annotation.DefaultPreview

@Composable
fun ThreeDotsLoadingView() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_three_dots_loading))
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier.width(80.dp).height(30.dp),
            iterations = LottieConstants.IterateForever,
        )

        Text(text = "LOADING", style = WishBoardTheme.typography.suitD2, color = WishBoardTheme.colors.gray700)
    }
}

@DefaultPreview
@Composable
fun PreviewThreeDotsLoadingView() {
    ThreeDotsLoadingView()
}
