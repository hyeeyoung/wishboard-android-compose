package com.hyeeyoung.wishboard.config.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.navgraph.folderNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.myNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.webViewNavGraph
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen.Upload.ARG_ITEM_DETAIL
import com.hyeeyoung.wishboard.presentation.calendar.screen.CalendarScreen
import com.hyeeyoung.wishboard.presentation.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.noti.NotiScreen
import com.hyeeyoung.wishboard.presentation.upload.WishItemUploadScreen
import com.hyeeyoung.wishboard.presentation.wish.WishItemDetailScreen
import com.hyeeyoung.wishboard.presentation.wish.WishlistScreen
import kotlinx.serialization.json.Json

@Composable
fun BottomBarNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(modifier = modifier, navController = navController, startDestination = MainScreen.Wishlist.route) {
        composable(route = MainScreen.Wishlist.route) {
            WishlistScreen(navController = navController)
        }
        composable(route = MainScreen.Calendar.route) {
            CalendarScreen(navController = navController)
        }
        folderNavGraph(navController)

        composable(
            route = MainScreen.Upload.routeWithArg,
            arguments = listOf(
                navArgument(ARG_ITEM_DETAIL) {
                    type = NavType.StringType
                    nullable = true
                },
            ),
        ) { backStackEntry ->
            backStackEntry.arguments?.let {
                val itemDetailStr = it.getString(ARG_ITEM_DETAIL)
                val itemDetail =
                    if (!itemDetailStr.isNullOrEmpty()) {
                        Json.decodeFromString<WishItemDetail>(itemDetailStr)
                    } else {
                        null
                    }

                WishItemUploadScreen(navController = navController, itemDetail = itemDetail)
            }
        }
        composable(route = MainScreen.Noti.route) {
            NotiScreen()
        }

        myNavGraph(navController)

        with(MainScreen.WishItemDetail) {
            composable(
                route = routeWithArg,
                arguments = listOf(navArgument(ARG_WISH_ITEM_ID) { type = NavType.LongType }),
            ) { backStackEntry ->
                backStackEntry.arguments?.let {
                    val itemId = it.getLong(ARG_WISH_ITEM_ID)
                    WishItemDetailScreen(navController, itemId = itemId)
                }
            }
        }

        webViewNavGraph(navController = navController)
    }
}
