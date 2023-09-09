package com.hyeeyoung.wishboard.config.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hyeeyoung.wishboard.config.navigation.navgraph.itemDetailNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.signNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.uploadNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.webViewNavGraph
import com.hyeeyoung.wishboard.config.navigation.screen.Calendar
import com.hyeeyoung.wishboard.config.navigation.screen.Cart
import com.hyeeyoung.wishboard.config.navigation.screen.Intro
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.presentation.calendar.screen.CalendarScreen
import com.hyeeyoung.wishboard.presentation.cart.CartScreen
import com.hyeeyoung.wishboard.presentation.intro.IntroScreen
import com.hyeeyoung.wishboard.presentation.main.MainScreen
import com.hyeeyoung.wishboard.presentation.my.PasswordChangeScreen
import com.hyeeyoung.wishboard.presentation.my.ProfileEditScreen

@Composable
fun WishBoardNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(modifier = modifier, navController = navController, startDestination = Intro.route) {
        composable(Intro.route) {
            IntroScreen(navController)
        }

        signNavGraph(navController)

        composable(MainScreen.Root.route) {
            MainScreen(navController, onClickAdd = { navController.navigate(MainScreen.Upload.route) })
        }

        composable(route = Calendar.route) {
            CalendarScreen(navController = navController)
        }

        composable(route = Cart.route) {
            CartScreen(navController = navController)
        }

        uploadNavGraph(navController)

        itemDetailNavGraph(navController)

        composable(route = MainScreen.MyProfile.route) {
            ProfileEditScreen(navController = navController)
        }

        composable(route = MainScreen.MyPasswordChange.route) {
            PasswordChangeScreen(navController = navController)
        }

        webViewNavGraph(navController = navController)
    }
}
