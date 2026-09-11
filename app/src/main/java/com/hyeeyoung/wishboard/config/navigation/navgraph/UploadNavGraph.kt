package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.navhost.snackbarComposable
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.sign.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.upload.screen.WishUploadScreen
import com.hyeeyoung.wishboard.presentation.util.extension.getBase64Json

fun NavGraphBuilder.uploadNavGraph(navController: NavHostController, snackbarHostState: SnackbarHostState) =
    snackbarComposable(
        snackbarHostState = snackbarHostState,
        route = MainScreen.Upload.routeWithArg,
        arguments = listOf(
            navArgument(MainScreen.Upload.ARG_ITEM_DETAIL) {
                type = NavType.StringType
                nullable = true
            },
        ),
        enterTransition = { slideInVertically(initialOffsetY = { it }) },
        exitTransition = { slideOutVertically(targetOffsetY = { -it }) },
        popEnterTransition = { slideInVertically(initialOffsetY = { -it }) },
        popExitTransition = { slideOutVertically(targetOffsetY = { it }) },
    ) { backStackEntry ->
        backStackEntry.arguments?.let {
            val itemDetail = it.getBase64Json<WishItemDetail>(MainScreen.Upload.ARG_ITEM_DETAIL)

            WishUploadScreen(navController = navController, itemDetail = itemDetail)
        }
    }
