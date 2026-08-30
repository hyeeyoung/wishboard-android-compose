package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.navhost.snackbarComposable
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.common.ImageDetailScreen
import com.hyeeyoung.wishboard.presentation.util.extension.getBase64Json
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.wish.screen.WishItemDetailScreen

fun NavGraphBuilder.itemDetailNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    snackbarComposable(
        snackbarHostState = snackbarHostState,
        route = MainScreen.WishItemDetail.routeWithArg,
        arguments = listOf(navArgument(MainScreen.WishItemDetail.ARG_WISH_ITEM_ID) { type = NavType.LongType }),
    ) { backStackEntry ->
        backStackEntry.arguments?.let {
            val itemId = it.getLong(MainScreen.WishItemDetail.ARG_WISH_ITEM_ID)
            WishItemDetailScreen(navController, itemId = itemId)
        }
    }

    snackbarComposable(
        snackbarHostState = snackbarHostState,
        route = MainScreen.ImageDetail.routeWithArg,
        arguments = listOf(
            navArgument(MainScreen.ImageDetail.ARG_IMAGES) { type = NavType.StringType },
            navArgument(MainScreen.ImageDetail.ARG_INITIAL_INDEX) { type = NavType.IntType; defaultValue = 0 },
        ),
    ) { backStackEntry ->
        backStackEntry.arguments?.let {
            val images = it.getBase64Json<List<String>>(MainScreen.ImageDetail.ARG_IMAGES) ?: return@snackbarComposable
            val initialIndex = it.getInt(MainScreen.ImageDetail.ARG_INITIAL_INDEX)
            ImageDetailScreen(
                images = images,
                initialIndex = initialIndex,
                onClickClose = { navController.safePopBackStack() },
            )
        }
    }
}
