package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hyeeyoung.wishboard.config.navigation.screen.Intro
import com.hyeeyoung.wishboard.config.navigation.screen.Main
import com.hyeeyoung.wishboard.presentation.intro.IntroScreen
import com.hyeeyoung.wishboard.presentation.main.MainScreen

@Composable
fun WishBoardNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(modifier = modifier, navController = navController, startDestination = Intro.route) {
        composable(Intro.route) {
            IntroScreen(navController)
        }

        signNavGraph(navController)

        composable(Main.Root.route) {
            MainScreen()
        }
    }
}
