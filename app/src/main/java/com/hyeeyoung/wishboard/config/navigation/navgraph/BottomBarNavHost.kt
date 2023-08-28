package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hyeeyoung.wishboard.config.navigation.screen.Main
import com.hyeeyoung.wishboard.presentation.home.HomeScreen

@Composable
fun BottomBarNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(modifier = modifier, navController = navController, startDestination = Main.Home.route) {
        composable(route = Main.Home.route) {
            HomeScreen()
        }
        composable(route = Main.Folder.route) {
            /** TODO */
        }
        composable(route = Main.Add.route) {
            /** TODO */
        }
        composable(route = Main.Noti.route) {
            /** TODO */
        }
        composable(route = Main.My.route) {
            /** TODO */
        }
    }
}
