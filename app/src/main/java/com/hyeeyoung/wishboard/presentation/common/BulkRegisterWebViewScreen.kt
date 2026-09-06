package com.hyeeyoung.wishboard.presentation.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack

private const val BULK_REGISTER_URL = "https://wishboard-app-web.vercel.app/"
private const val JS_BRIDGE_NAME = "WishboardBridge"

@Composable
fun BulkRegisterWebViewScreen(
    navController: NavHostController,
    viewModel: BulkRegisterWebViewViewModel = hiltViewModel(),
) {
    val token by viewModel.webViewToken.collectAsStateWithLifecycle()
    val webViewRef: MutableState<WebView?> = remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.bridgeResponse.collect { r ->
            val js = if (r.token != null && r.deviceInfo != null) {
                "window.__WISHBOARD_BRIDGE__.resolve(${r.requestId}, " +
                    "{token:'${r.token}', deviceInfo:'${r.deviceInfo}'})"
            } else {
                "window.__WISHBOARD_BRIDGE__.resolve(${r.requestId}, " +
                    "{error:'REAUTH_REQUIRED'})"
            }
            webViewRef.value?.evaluateJavascript(js, null)
        }
    }

    BackHandler { navController.safePopBackStack() }

    Scaffold(
        topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(
                    startIcon = WishBoardTopBarModel.TopBarIcon.CLOSE,
                    onClickStartIcon = { navController.safePopBackStack() },
                ),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            if (token != null) {
                BulkRegisterWebView(
                    token = token!!,
                    deviceInfo = viewModel.deviceInfo,
                    userAgent = viewModel.userAgent,
                    webViewRef = webViewRef,
                    onMessage = viewModel::handleBridgeMessage,
                    exitWebView = { navController.safePopBackStack() },
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = WishBoardTheme.colors.gray700,
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun BulkRegisterWebView(
    token: String,
    deviceInfo: String,
    userAgent: String,
    webViewRef: MutableState<WebView?>,
    onMessage: (String) -> Unit,
    exitWebView: () -> Unit,
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                settings.apply {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    userAgentString = "$userAgentString $userAgent"
                }
                addJavascriptInterface(
                    WishboardBridge(onMessage = onMessage),
                    JS_BRIDGE_NAME,
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        view?.evaluateJavascript(
                            "window.__WISHBOARD_TOKEN__='$token'; window.__WISHBOARD_DEVICE_INFO__='$deviceInfo';",
                            null,
                        )
                    }
                }
                loadUrl(BULK_REGISTER_URL)
                webViewRef.value = this
            }
        },
    )

    BackHandler {
        if (webViewRef.value?.canGoBack() == true) {
            webViewRef.value?.goBack()
        } else {
            exitWebView()
        }
    }
}

private class WishboardBridge(private val onMessage: (String) -> Unit) {
    @JavascriptInterface
    fun postMessage(json: String) = onMessage(json)
}
