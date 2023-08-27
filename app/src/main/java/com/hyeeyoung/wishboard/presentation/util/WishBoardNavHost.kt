package com.hyeeyoung.wishboard.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.presentation.intro.IntroScreen
import com.hyeeyoung.wishboard.presentation.main.MainScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignMainScreen

@Composable
fun WishBoardNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(modifier = modifier, navController = navController, startDestination = "intro") {
        composable("intro") {
            IntroScreen(onNavigateToNext = { isLogin ->
                val nextScreen = if (isLogin) "main" else "signMain"
                navController.navigate(nextScreen)
            })
        }
        composable("signMain") {
            SignMainScreen(onNavigateToSignIn = { navController.navigate("signIn") })
        }
        composable("signIn") {
            SignInScreen(onClickBack = { navController.popBackStack() })
        }
        composable("main") {
            MainScreen()
        }
    }
}
