package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.my.MyScreen
import com.hyeeyoung.wishboard.presentation.my.PasswordChangeScreen
import com.hyeeyoung.wishboard.presentation.my.ProfileEditScreen

fun NavGraphBuilder.myNavGraph(navController: NavHostController) =
    navigation(startDestination = MainScreen.My.getStartRouteForMainTab(), route = MainScreen.My.route) {
        composable(route = MainScreen.My.getStartRouteForMainTab()) {
            MyScreen(navController = navController)
        }

        composable(route = MainScreen.MyProfile.route) {
            ProfileEditScreen(navController = navController)
        }

        composable(route = MainScreen.MyPasswordChange.route) {
            PasswordChangeScreen(navController = navController)
        }
    }
