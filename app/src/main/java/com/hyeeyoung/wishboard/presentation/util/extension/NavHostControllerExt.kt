package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.config.navigation.screen.WebView
import com.hyeeyoung.wishboard.config.navigation.screen.WebView.ARG_TITLE
import com.hyeeyoung.wishboard.config.navigation.screen.WebView.ARG_URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun NavHostController.moveToWebView(title: String? = null, url: String) {
    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
    val titleArgUrl = if (title != null) "&$ARG_TITLE=$title" else ""
    val route = "${WebView.route}?$ARG_URL=$encodedUrl$titleArgUrl"
    navigate(route)
}
