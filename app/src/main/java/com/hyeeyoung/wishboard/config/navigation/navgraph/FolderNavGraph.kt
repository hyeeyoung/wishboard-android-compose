package com.hyeeyoung.wishboard.config.navigation.navgraph

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
            FolderScreen(navController = bottomNavController)
        }

        composable(
            route = MainScreen.FolderDetail.routeWithArg,
            arguments = listOf(
                navArgument(MainScreen.FolderDetail.ARG_FOLDER_ID) { type = NavType.LongType },
                navArgument(MainScreen.FolderDetail.ARG_FOLDER_NAME) { type = NavType.StringType },
            ),
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
