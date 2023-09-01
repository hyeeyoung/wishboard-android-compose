package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.screen.Main
import com.hyeeyoung.wishboard.presentation.folder.FolderDetailScreen
import com.hyeeyoung.wishboard.presentation.folder.FolderScreen

fun NavGraphBuilder.folderNavGraph(navController: NavHostController) =
    navigation(startDestination = Main.Folder.makeStartRoute(), route = Main.Folder.route) {
        composable(route = Main.Folder.makeStartRoute()) {
            FolderScreen(navController = navController)
        }

        with(Main.FolderDetail) {
            composable(
                route = routeWithArg,
                arguments = listOf(
                    navArgument(ARG_FOLDER_ID) { type = NavType.LongType },
                    navArgument(ARG_FOLDER_NAME) { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                backStackEntry.arguments?.let {
                    val id = it.getLong(ARG_FOLDER_ID)
                    val name =
                        it.getString(ARG_FOLDER_NAME) ?: throw NullPointerException("폴더명이 존재하지 않습니다.")
                    FolderDetailScreen(navController = navController, folderId = id, folderName = name)
                }
            }
        }
    }
