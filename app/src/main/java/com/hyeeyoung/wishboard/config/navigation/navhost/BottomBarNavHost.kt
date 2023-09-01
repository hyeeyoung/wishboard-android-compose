package com.hyeeyoung.wishboard.config.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.navgraph.folderNavGraph
import com.hyeeyoung.wishboard.config.navigation.screen.Main
import com.hyeeyoung.wishboard.presentation.noti.NotiScreen
import com.hyeeyoung.wishboard.presentation.wish.WishItemDetailScreen
import com.hyeeyoung.wishboard.presentation.wish.WishlistScreen

@Composable
fun BottomBarNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(modifier = modifier, navController = navController, startDestination = Main.Wishlist.route) {
        composable(route = Main.Wishlist.route) {
            WishlistScreen()
        }

        folderNavGraph(navController)

        composable(route = Main.Add.route) {
            /** TODO */
        }
        composable(route = Main.Noti.route) {
            NotiScreen()
        }
        composable(route = Main.My.route) {
            /** TODO */
        }

        with(Main.WishItemDetail) {
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
    }
}
