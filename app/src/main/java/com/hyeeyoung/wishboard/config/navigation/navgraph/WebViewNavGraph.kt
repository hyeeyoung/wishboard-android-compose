package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.screen.WebView
import com.hyeeyoung.wishboard.presentation.WebViewScreen

fun NavGraphBuilder.webViewNavGraph(navController: NavHostController) = composable(
    route = WebView.routeWithArg,
    arguments = listOf(
        navArgument(WebView.ARG_TITLE_RES) { type = NavType.IntType },
        navArgument(WebView.ARG_URL) { type = NavType.StringType },
    ),
) { backStackEntry ->
    backStackEntry.arguments?.let {
        val titleRes = it.getInt(WebView.ARG_TITLE_RES) ?: throw NullPointerException("타이틀 없음")
        val url = it.getString(WebView.ARG_URL) ?: throw NullPointerException("url 없음")
        WebViewScreen(navController = navController, url = url, title = titleRes)
    }
}
