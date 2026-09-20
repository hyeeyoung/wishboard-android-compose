package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.folder.FolderDetailScreen
import com.hyeeyoung.wishboard.presentation.folder.FolderScreen

fun NavGraphBuilder.folderNavGraph(bottomNavController: NavHostController, wishNavController: NavHostController) =
    navigation(startDestination = MainScreen.Folder.getStartRouteForMainTab(), route = MainScreen.Folder.route) {
        composable(route = MainScreen.Folder.getStartRouteForMainTab()) {
            FolderScreen(bottomNavController = bottomNavController, wishNavController = wishNavController)
        }

        composable(
            route = MainScreen.FolderDetail.routeWithArg,
            arguments = listOf(
                navArgument(MainScreen.FolderDetail.ARG_FOLDER_ID) { type = NavType.LongType },
                navArgument(MainScreen.FolderDetail.ARG_FOLDER_NAME) { type = NavType.StringType },
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
        ) { backStackEntry ->
            backStackEntry.arguments?.let {
                val id = it.getLong(MainScreen.FolderDetail.ARG_FOLDER_ID)
                val name =
                    it.getString(MainScreen.FolderDetail.ARG_FOLDER_NAME)
                        ?: throw NullPointerException("폴더명이 존재하지 않습니다.")
                FolderDetailScreen(
                    bottomNavController = bottomNavController,
                    wishNavController = wishNavController,
                    folderId = id,
                    folderName = name,
                )
            }
        }
    }
