package com.hyeeyoung.wishboard.presentation.common

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.ViewGroup
import android.webkit.WebResourceRequest
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.LegacyWishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack

@Composable
fun WebViewScreen(
    navController: NavHostController,
    url: String,
    title: String? = null,
) {
    val systemUiController = rememberSystemUiController()
    val webView: MutableState<WebView?> = remember { mutableStateOf(null) }

    SideEffect {
        systemUiController.setNavigationBarColor(color = Color.White)
    }

    Scaffold(topBar = {
        WebViewTopBar(
            title = title,
            onClickClose = { navController.safePopBackStack() },
            onClickRefresh = { webView.value?.reload() },
        )
    }) { paddingValues ->
        Column(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
            WishBoardDivider()
            WebView(
                webView = webView,
                modifier = Modifier.fillMaxSize(),
                url = url,
                exitWebView = { navController.safePopBackStack() },
            )
        }
    }
}

@Composable
fun WebViewTopBar(title: String?, onClickClose: () -> Unit, onClickRefresh: () -> Unit) {
    WishBoardTopBar(
        topBarModel = WishBoardTopBarModel(
            startIcon = WishBoardTopBarModel.TopBarIcon.CLOSE,
            title = title ?: "",
            onClickStartIcon = { onClickClose() },
        ),
        endComponent = { endComponentModifier ->
            Row(modifier = endComponentModifier) {
                LegacyWishBoardIconButton(iconRes = R.drawable.ic_refresh, onClick = { onClickRefresh() })
//                WishBoardIconButton(iconRes = R.drawable.ic_more, onClick = { /*TODO*/ })
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
                webViewClient = object : WebViewClient() {
                    // 쇼핑몰 단축 URL(naver.me 등)이 자체 앱으로 열기 위한 커스텀 스킴으로 리다이렉트하는
                    // 경우가 있는데, WebView는 http(s)가 아닌 스킴을 로드하지 못해 ERR_UNKNOWN_URL_SCHEME로
                    // 실패한다. http(s)가 아니면 외부 앱(Intent)으로 넘겨 처리한다.
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val uri = request.url
                        if (uri.scheme == "http" || uri.scheme == "https") {
                            return false
                        }

                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } catch (e: ActivityNotFoundException) {
                            // 처리할 수 있는 앱이 없으면 무시한다.
                        }
                        return true
                    }
                }
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

    BackHandler {
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
