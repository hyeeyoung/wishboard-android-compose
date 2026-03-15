package com.hyeeyoung.wishboard.config.navigation.navhost

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hyeeyoung.wishboard.config.navigation.navgraph.itemDetailNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.signNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.uploadNavGraph
import com.hyeeyoung.wishboard.config.navigation.navgraph.webViewNavGraph
import com.hyeeyoung.wishboard.config.navigation.screen.Calendar
import com.hyeeyoung.wishboard.config.navigation.screen.Intro
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.LocalSnackbarHostState
import com.hyeeyoung.wishboard.designsystem.component.WishBoardSnackbarHost
import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import com.hyeeyoung.wishboard.presentation.calendar.screen.CalendarScreen
import com.hyeeyoung.wishboard.presentation.intro.IntroScreen
import com.hyeeyoung.wishboard.presentation.main.MainScreen
import com.hyeeyoung.wishboard.presentation.my.screen.PasswordChangeScreen
import com.hyeeyoung.wishboard.presentation.my.screen.ProfileEditScreen
import com.hyeeyoung.wishboard.presentation.noti.NotiScreen
import com.hyeeyoung.wishboard.presentation.util.extension.getBase64Json

@Composable
fun WishBoardNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    val snackbarHostState = LocalSnackbarHostState.current

    NavHost(modifier = modifier.systemBarsPadding(), navController = navController, startDestination = Intro.route) {
        snackbarComposable(snackbarHostState = snackbarHostState, route = Intro.route) {
            IntroScreen(navController = navController)
        }

        snackbarComposable(
            snackbarHostState = snackbarHostState,
            route = MainScreen.Root.routeWithArg,
            arguments = listOf(navArgument(MainScreen.Root.ARG_IS_FIRST_LAUNCH) { type = NavType.BoolType }),
        ) { backStackEntry ->
            backStackEntry.arguments?.let {
                MainScreen(
                    wishNavController = navController,
                    isFirstLaunch = it.getBoolean(MainScreen.Root.ARG_IS_FIRST_LAUNCH),
                    onClickAdd = { navController.navigate(MainScreen.Upload.route) },
                )
            }
        }

        snackbarComposable(snackbarHostState = snackbarHostState, route = MainScreen.Noti.route) {
            NotiScreen(navController = navController)
        }

        snackbarComposable(
            snackbarHostState = snackbarHostState,
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { -it }) },
            popEnterTransition = { slideInVertically(initialOffsetY = { -it }) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it }) },
            route = Calendar.route,
        ) {
            CalendarScreen(navController = navController)
        }

        snackbarComposable(
            snackbarHostState = snackbarHostState,
            route = MainScreen.MyProfile.routeWithArg,
            arguments = listOf(
                navArgument(MainScreen.MyProfile.ARG_USER_INFO) {
                    type = NavType.StringType
                },
            ),
        ) { backStackEntry ->
            backStackEntry.arguments?.let {
                val profileInfo =
                    it.getBase64Json<UserInfo>(MainScreen.MyProfile.ARG_USER_INFO) ?: return@snackbarComposable
                ProfileEditScreen(navController = navController, userInfo = profileInfo)
            }
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
    enterTransition:
    (
        @JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        null,
    exitTransition:
    (
        @JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        null,
    popEnterTransition:
    (
        @JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        enterTransition,
    popExitTransition:
    (
        @JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
    ) { navBackStackEntry ->
        Scaffold(
            snackbarHost = { WishBoardSnackbarHost(hostState = snackbarHostState) },
            content = { content(navBackStackEntry) },
        )
    }
}
