package com.hyeeyoung.wishboard.config.navigation.navhost

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hyeeyoung.wishboard.config.navigation.navgraph.itemDetailNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.signNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.uploadNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.webViewNavGraph
import com.hyeeyoung.wishboard.config.navigation.screen.Calendar
import com.hyeeyoung.wishboard.config.navigation.screen.Intro
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.LocalSnackbarHostState
import com.hyeeyoung.wishboard.designsystem.component.WishBoardSnackbarHost
import com.hyeeyoung.wishboard.presentation.calendar.screen.CalendarScreen
import com.hyeeyoung.wishboard.presentation.intro.IntroScreen
import com.hyeeyoung.wishboard.presentation.main.MainScreen
import com.hyeeyoung.wishboard.presentation.my.PasswordChangeScreen
import com.hyeeyoung.wishboard.presentation.my.ProfileEditScreen

@Composable
fun WishBoardNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    val snackbarHostState = LocalSnackbarHostState.current

    NavHost(modifier = modifier, navController = navController, startDestination = Intro.route) {
        snackbarComposable(snackbarHostState = snackbarHostState, route = Intro.route) {
            IntroScreen(navController)
        }

        snackbarComposable(snackbarHostState = snackbarHostState, route = MainScreen.Root.route) {
            MainScreen(navController, onClickAdd = { navController.navigate(MainScreen.Upload.route) })
        }

        snackbarComposable(snackbarHostState = snackbarHostState, route = Calendar.route) {
            CalendarScreen(navController = navController)
        }

        snackbarComposable(snackbarHostState = snackbarHostState, route = MainScreen.MyProfile.route) {
            ProfileEditScreen(navController = navController)
        }

        snackbarComposable(snackbarHostState = snackbarHostState, route = MainScreen.MyPasswordChange.route) {
            PasswordChangeScreen(navController = navController)
        }

        signNavGraph(navController = navController, snackbarHostState = snackbarHostState)

        uploadNavGraph(navController = navController, snackbarHostState = snackbarHostState)

        itemDetailNavGraph(navController = navController, snackbarHostState = snackbarHostState)

        webViewNavGraph(navController = navController, snackbarHostState = snackbarHostState)
//        composable(route = Cart.route) {
//            CartScreen(navController = navController)
//        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun NavGraphBuilder.snackbarComposable(
    snackbarHostState: SnackbarHostState,
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(route = route, arguments = arguments) { navBackStackEntry ->
        Scaffold(
            snackbarHost = { WishBoardSnackbarHost(hostState = snackbarHostState) },
            content = { content(navBackStackEntry) })
    }
}