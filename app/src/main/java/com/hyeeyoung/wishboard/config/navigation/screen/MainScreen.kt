package com.hyeeyoung.wishboard.config.navigation.screen

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hyeeyoung.wishboard.config.GlobalState

sealed class MainScreen(override val route: String) : Screen {
    data object Root : MainScreen(route = "mainRoot") {
        const val ARG_IS_FIRST_LAUNCH: String = "isFirstLaunch"
        val routeWithArg = "$route/{$ARG_IS_FIRST_LAUNCH}"
    }

    data object Wishlist : MainScreen(route = "wishlist")

    data object WishItemDetail : MainScreen(route = "wishItemDetail") {
        const val ARG_WISH_ITEM_ID: String = "wishItemId"
        val routeWithArg = "$route/{$ARG_WISH_ITEM_ID}"
    }

    data object Folder : MainScreen(route = "folder")

    data object FolderDetail : MainScreen(route = "folderDetail") {
        const val ARG_FOLDER_ID: String = "folderId"
        const val ARG_FOLDER_NAME: String = "folderName"
        val routeWithArg = "$route/{$ARG_FOLDER_ID}/{$ARG_FOLDER_NAME}"
    }

    data object Upload : MainScreen(route = "upload") {
        const val ARG_ITEM_DETAIL: String = "itemDetail"
        val routeWithArg = "$route?$ARG_ITEM_DETAIL={$ARG_ITEM_DETAIL}"
    }

    data object Noti : MainScreen(route = "noti")

    data object My : MainScreen(route = "my")

    data object MyProfile : MainScreen(route = "myProfile") {
        const val ARG_USER_INFO: String = "userInfo"
        val routeWithArg = "$route/{$ARG_USER_INFO}"
    }

    data object MyPasswordChange : MainScreen(route = "myPasswordChange")

    /** 메인 바텀바 메뉴에서 해당 메뉴의 시작 루트를 반환. 단, NavGraphBuilder.navigation() 사용 시 startDestination 경로는 파라미터 route + "start" 합성 */
    fun getStartRouteForMainTab(): String = when (this) {
        Wishlist, Upload, Noti, My -> this.route
        Folder -> this.route + "Start"
        else -> throw IllegalStateException("StartDestination이 정의되어있지 않음.")
    }

    fun getRealRoute(): String? =
        when (this) {
            Wishlist -> Wishlist.route
            Folder -> Folder.getStartRouteForMainTab()
            FolderDetail -> FolderDetail.routeWithArg
            Upload -> Upload.route
            My -> My.route
            else -> null
        }

    @Composable
    fun ScrollToTopEffect(state: LazyGridState) {
        val route by GlobalState.reselectedBottomBarRoute.collectAsStateWithLifecycle()
        LaunchedEffect(route) {
            if (route == getRealRoute()) {
                state.animateScrollToItem(0)
                GlobalState.reselectedBottomBarRoute.value = null
            }
        }
    }

    @Composable
    fun ScrollToTopEffect(state: LazyListState) {
        val route by GlobalState.reselectedBottomBarRoute.collectAsStateWithLifecycle()
        LaunchedEffect(route) {
            if (route == getRealRoute()) {
                state.animateScrollToItem(0)
                GlobalState.reselectedBottomBarRoute.value = null
            }
        }
    }
}
