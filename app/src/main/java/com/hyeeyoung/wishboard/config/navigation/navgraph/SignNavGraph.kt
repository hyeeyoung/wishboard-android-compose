package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hyeeyoung.wishboard.config.navigation.screen.Main
import com.hyeeyoung.wishboard.config.navigation.screen.Sign
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInVerificationCodeScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignMainScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpPasswordScreen

fun NavGraphBuilder.signNavGraph(navController: NavHostController) =
    navigation(startDestination = Sign.Main.route, route = Sign.Root.route) {
        composable(Sign.Main.route) {
            SignMainScreen(
                onClickSignUp = { navController.navigate(Sign.SignUp.route) },
                onClickLogin = { navController.navigate(Sign.Login.route) },
            )
        }

        navigation(startDestination = Sign.Email.route, route = Sign.SignUp.route) {
            composable(Sign.Email.route) {
                SignUpEmailScreen(
                    onClickBack = { navController.popBackStack() },
                    onClickNext = { navController.navigate(Sign.Password.route) },
                )
            }
            composable(Sign.Password.route) {
                SignUpPasswordScreen(
                    onClickBack = { navController.popBackStack() },
                    onClickSignUp = {
                        navController.navigate(Main.Root.route) {
                            popUpTo(route = Sign.Root.route) {
                                inclusive = true
                            }
                        }
                    },
                )
            }
        }
        composable(Sign.Login.route) {
            SignInScreen(
                onClickBack = { navController.popBackStack() },
                onClickLogin = {
                    navController.navigate(Main.Root.route) {
                        popUpTo(route = Sign.Root.route) {
                            inclusive = true
                        }
                    }
                },
                onClickForgotPassword = { navController.navigate(Sign.EmailLogin.route) },
            )
        }

        navigation(startDestination = Sign.Email.route, route = Sign.EmailLogin.route) {
            composable(Sign.Email.route) {
                SignInEmailScreen(
                    onClickBack = { navController.popBackStack() },
                    onClickReceiveEmail = { navController.navigate(Sign.Verification.route) },
                )
            }
            composable(Sign.Verification.route) {
                SignInVerificationCodeScreen(
                    onClickBack = { navController.popBackStack() },
                    onClickLogin = {
                        navController.navigate(Main.Root.route) {
                            popUpTo(route = Sign.Root.route) {
                                inclusive = true
                            }
                        }
                    },
                )
            }
        }
    }
