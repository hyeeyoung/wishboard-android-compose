package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.hyeeyoung.wishboard.config.navigation.screen.Main
import com.hyeeyoung.wishboard.presentation.my.MyScreen
import com.hyeeyoung.wishboard.presentation.my.PasswordChangeScreen
import com.hyeeyoung.wishboard.presentation.my.ProfileEditScreen

fun NavGraphBuilder.myNavGraph(navController: NavHostController) =
    navigation(startDestination = Main.My.makeStartRoute(), route = Main.My.route) {
        composable(route = Main.My.makeStartRoute()) {
            MyScreen(navController = navController)
        }

        composable(route = Main.MyProfile.route) {
            ProfileEditScreen(navController = navController)
        }

        composable(route = Main.MyPasswordChange.route) {
            PasswordChangeScreen(navController = navController)
        }
    }
