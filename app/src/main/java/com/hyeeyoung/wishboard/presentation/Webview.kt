package com.hyeeyoung.wishboard.presentation

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel

@Composable
fun WebViewScreen(
    navController: NavHostController,
    url: String,
    title: String? = null,
) {
    val webView: MutableState<WebView?> = remember { mutableStateOf(null) }

    Scaffold(topBar = {
        WebViewTopBar(
            title = title,
            onClickClose = { navController.popBackStack() },
            onClickRefresh = { webView.value?.reload() },
        )
    }) { paddingValues ->
        Column(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
            WishBoardDivider()
            WebView(
                webView = webView,
                modifier = Modifier.fillMaxSize(),
                url = url,
                exitWebView = { navController.popBackStack() },
            )
        }
    }
}

@Composable
fun WebViewTopBar(title: String?, onClickClose: () -> Unit, onClickRefresh: () -> Unit) {
    WishBoardTopBar(
        topBarModel = WishBoardTopBarModel(
            startIcons = listOf(WishBoardTopBarModel.TopBarIcon.CLOSE),
            title = title ?: "",
            onClickStartIcon = { onClickClose() },
        ),
        endComponent = { endComponentModifier ->
            Row(modifier = endComponentModifier) {
                WishBoardIconButton(iconRes = R.drawable.ic_refresh, onClick = { onClickRefresh() })
                WishBoardIconButton(iconRes = R.drawable.ic_more, onClick = { /*TODO*/ })
                Spacer(modifier = Modifier.size(4.dp))
            }
        },
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(
    webView: MutableState<WebView?>,
    modifier: Modifier = Modifier,
    url: String,
    exitWebView: () -> Unit,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    domStorageEnabled = true
                    setSupportMultipleWindows(false)
                    setSupportZoom(true)
                }

                loadUrl(url)
                webView.value = this
            }
        },
    )

    BackHandler() {
        if (webView.value?.canGoBack() == true) {
            webView.value?.goBack()
        } else {
            exitWebView()
        }
    }
}

@Preview
@Composable
fun PreviewTopBar() {
    WebViewTopBar(title = "youngjin.com", onClickClose = { /*TODO*/ }, onClickRefresh = {})
}
