package com.hyeeyoung.wishboard.config.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hyeeyoung.wishboard.config.navigation.navgraph.folderNavGraph
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.my.MyScreen
import com.hyeeyoung.wishboard.presentation.noti.NotiScreen
import com.hyeeyoung.wishboard.presentation.wish.screen.WishlistScreen

/** 바텀바가 보이는 화면 전용 네비게이션 그래프를 정의 */
@Composable
fun BottomBarNavHost(
    modifier: Modifier = Modifier,
    bottomNavController: NavHostController,
    wishNavController: NavHostController,
) {
    NavHost(modifier = modifier, navController = bottomNavController, startDestination = MainScreen.Wishlist.route) {
        // 메인 탭
        composable(route = MainScreen.Wishlist.route) {
            WishlistScreen(navController = wishNavController)
        }

        folderNavGraph(bottomNavController = bottomNavController, wishNavController = wishNavController)

        composable(route = MainScreen.Noti.route) {
            NotiScreen(navController = wishNavController)
        }

        composable(route = MainScreen.My.route) {
            MyScreen(navController = wishNavController)
        }
    }
}
