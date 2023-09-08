package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.upload.WishUploadScreen
import kotlinx.serialization.json.Json

fun NavGraphBuilder.uploadNavGraph(navController: NavHostController) = composable(
    route = MainScreen.Upload.routeWithArg,
    arguments = listOf(
        navArgument(MainScreen.Upload.ARG_ITEM_DETAIL) {
            type = NavType.StringType
            nullable = true
        },
    ),
) { backStackEntry ->
    backStackEntry.arguments?.let {
        val itemDetailStr = it.getString(MainScreen.Upload.ARG_ITEM_DETAIL)
        val itemDetail =
            if (!itemDetailStr.isNullOrEmpty()) {
                Json.decodeFromString<WishItemDetail>(itemDetailStr)
            } else {
                null
            }

        WishUploadScreen(navController = navController, itemDetail = itemDetail)
    }
}
