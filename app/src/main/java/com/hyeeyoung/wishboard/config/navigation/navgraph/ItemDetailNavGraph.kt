package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.navhost.snackbarComposable
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.wish.screen.WishItemDetailScreen

fun NavGraphBuilder.itemDetailNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) = snackbarComposable(
    snackbarHostState = snackbarHostState,
    route = MainScreen.WishItemDetail.routeWithArg,
    arguments = listOf(navArgument(MainScreen.WishItemDetail.ARG_WISH_ITEM_ID) { type = NavType.LongType }),
) { backStackEntry ->
    backStackEntry.arguments?.let {
        val itemId = it.getLong(MainScreen.WishItemDetail.ARG_WISH_ITEM_ID)
        WishItemDetailScreen(navController, itemId = itemId)
    }
}
