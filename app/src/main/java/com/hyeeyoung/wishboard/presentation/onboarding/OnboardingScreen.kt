package com.hyeeyoung.wishboard.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingModalContent(onDismissRequest: () -> Unit) {
    val onboardingRes = OnboardingRes.values()
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Column(modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
            HorizontalPager(pageCount = onboardingRes.size, state = pagerState) {
                OnboardingItem(onboardingRes[pagerState.currentPage])
            }
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Indicator
                Row(modifier = Modifier.padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(onboardingRes.size) { pos ->
                        val color = if (pos == pagerState.currentPage) {
                            WishBoardTheme.colors.gray700
                        } else {
                            WishBoardTheme.colors.gray100
                        }
                        Canvas(modifier = Modifier.size(6.dp), onDraw = { drawCircle(color = color) })
                    }
                }

                WishBoardWideButton(
                    enabled = pagerState.currentPage == onboardingRes.lastIndex,
                    onClick = { onDismissRequest() },
                    text = stringResource(id = R.string.onboarding_yes_btn_text),
                    isGreen = false,
                )
            }
        }
    }
}

@Composable
fun OnboardingItem(onboardingRes: OnboardingRes) {
    Column {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .background(WishBoardTheme.colors.gray100),
            painter = painterResource(id = onboardingRes.imageRes),
            contentDescription = null,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WishBoardTheme.colors.white)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(id = onboardingRes.titleRes),
                style = WishBoardTheme.typography.suitH1,
                color = WishBoardTheme.colors.gray700,
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(id = onboardingRes.descriptionRes),
                style = WishBoardTheme.typography.suitD2M,
                color = WishBoardTheme.colors.gray300,
                textAlign = TextAlign.Center,
            )
        }
    }
}

enum class OnboardingRes(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int,
) {
    SAVE_ITEM(
        R.string.onboarding_save_item_title,
        R.string.onboarding_save_item_description,
        R.drawable.img_onboarding_save_item,
    ),
    FOLDER_SETTING(
        R.string.onboarding_folder_setting_title,
        R.string.onboarding_folder_setting_description,
        R.drawable.img_onboarding_folder_setting,
    ),
    NOTI_SETTING(
        R.string.onboarding_noti_setting_title,
        R.string.onboarding_noti_setting_description,
        R.drawable.img_onboarding_noti_setting,
    ),
}

@Preview(showSystemUi = true)
@Composable
fun PreviewOnboardingScreen() {
    OnboardingModalContent(onDismissRequest = {})
}
