package com.hyeeyoung.wishboard.config.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.config.navigation.navgraph.signNavGraph
import com.hyeeyoung.wishboard.presentation.intro.IntroScreen
import com.hyeeyoung.wishboard.presentation.main.MainScreen

@Composable
fun WishBoardNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(modifier = modifier, navController = navController, startDestination = Navigation.INTRO.name) {
        composable(Navigation.INTRO.name) {
            IntroScreen(onNavigateToNext = { isLogin ->
                val nextScreen = if (isLogin) Navigation.MAIN.name else Navigation.SIGN.name
                navController.navigate(nextScreen) { popUpTo(navController.graph.id) }
            })
        }

        signNavGraph(navController = navController)

        // Main
        composable("main") {
            MainScreen()
        }
    }
}
