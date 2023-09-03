package com.hyeeyoung.wishboard.presentation

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel

@Composable
fun WebViewScreen(
    navController: NavHostController,
    url: String,
    @StringRes title: Int,
) {
    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                title = stringResource(id = title),
                onClickStartIcon = { navController.popBackStack() },
            ),
        )
    }) { paddingValues ->
        Column(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
            WishBoardDivider()
            WebView(modifier = Modifier.fillMaxSize(), url = url)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(
    modifier: Modifier = Modifier,
    url: String,
) {
    var webView: WebView? = null
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                settings.apply {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    domStorageEnabled = true
                    setSupportZoom(true)
                }

                loadUrl(url)
                webView = this
            }
        },
        update = {
            webView = it
        },
    )
}

@Preview
@Composable
fun PreviewWdbViewScreen() {
    WebViewScreen(
        navController = rememberNavController(),
        url = "https://www.naver.com",
        title = R.string.my_menu_terms,
    )
}
