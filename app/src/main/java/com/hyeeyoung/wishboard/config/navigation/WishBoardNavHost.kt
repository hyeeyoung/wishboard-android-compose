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
import timber.log.Timber

@Composable
fun WishBoardNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(modifier = modifier, navController = navController, startDestination = Route.Intro.toRoute()) {
        composable(Route.Intro.toRoute()) {
            IntroScreen(onNavigateToNext = { isLogin ->
                val nextScreen = if (isLogin) Route.Main.toRoute() else Route.Sign.toRoute()
                Timber.e(nextScreen)
                navController.navigate(nextScreen) { popUpTo(navController.graph.id) }
            })
        }

        signNavGraph(navController = navController)

        composable(Route.Main.toRoute()) {
            Timber.e(Route.Main.toRoute())
            MainScreen()
        }
    }
}
