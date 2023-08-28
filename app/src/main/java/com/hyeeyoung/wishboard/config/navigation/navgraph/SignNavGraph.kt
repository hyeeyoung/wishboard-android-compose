package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hyeeyoung.wishboard.config.navigation.Route
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInVerificationCodeScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignMainScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpPasswordScreen

fun NavGraphBuilder.signNavGraph(navController: NavHostController) =
    navigation(startDestination = Route.Sign.main, route = Route.Sign.toRoute()) {
        composable(Route.Sign.main) {
            SignMainScreen(
                onClickSignUp = { navController.navigate(Route.Sign.signUp) },
                onClickLogin = { navController.navigate(Route.Sign.login) },
            )
        }

        navigation(startDestination = Route.Sign.email, route = Route.Sign.signUp) {
            composable(Route.Sign.email) {
                SignUpEmailScreen(
                    onClickBack = { navController.popBackStack() },
                    onClickNext = { navController.navigate(Route.Sign.password) },
                )
            }
            composable(Route.Sign.password) {
                SignUpPasswordScreen(
                    onClickBack = { navController.popBackStack() },
                    onClickSignUp = {
                        navController.navigate(Route.Main.toRoute()) {
                            popUpTo(route = Route.Sign.toRoute()) {
                                inclusive = true
                            }
                        }
                    },
                )
            }
        }

        composable(Route.Sign.login) {
            SignInScreen(
                onClickBack = { navController.popBackStack() },
                onClickLogin = {
                    navController.navigate(Route.Main.toRoute()) {
                        popUpTo(route = Route.Sign.toRoute()) {
                            inclusive = true
                        }
                    }
                },
                onClickForgotPassword = { navController.navigate(Route.Sign.emailLogin) },
            )
        }

        navigation(startDestination = Route.Sign.email, route = Route.Sign.emailLogin) {
            composable(Route.Sign.email) {
                SignInEmailScreen(
                    onClickBack = { navController.popBackStack() },
                    onClickReceiveEmail = { navController.navigate(Route.Sign.verification) },
                )
            }
            composable(Route.Sign.verification) {
                SignInVerificationCodeScreen(
                    onClickBack = { navController.popBackStack() },
                    onClickLogin = {
                        navController.navigate(Route.Main.toRoute()) {
                            popUpTo(route = Route.Sign.toRoute()) {
                                inclusive = true
                            }
                        }
                    },
                )
            }
        }
    }
