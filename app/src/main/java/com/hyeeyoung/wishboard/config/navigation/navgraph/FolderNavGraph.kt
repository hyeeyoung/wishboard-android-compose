package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.navhost.snackbarComposable
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.folder.FolderDetailScreen
import com.hyeeyoung.wishboard.presentation.folder.FolderScreen

fun NavGraphBuilder.folderNavGraph(wishNavController: NavHostController) =
    navigation(startDestination = MainScreen.Folder.getStartRouteForMainTab(), route = MainScreen.Folder.route) {
        composable(route = MainScreen.Folder.getStartRouteForMainTab()) {
            FolderScreen(wishNavController = wishNavController)
        }
    }

/** 폴더 상세는 바텀바가 없는 화면이므로 바텀바 탭 그래프가 아닌 최상위 네비게이션 호스트에 등록한다 */
fun NavGraphBuilder.folderDetailNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    snackbarComposable(
        snackbarHostState = snackbarHostState,
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
                navController = navController,
                folderId = id,
                folderName = name,
            )
        }
    }
}
