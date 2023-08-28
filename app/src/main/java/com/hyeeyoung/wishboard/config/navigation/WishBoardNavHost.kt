package com.hyeeyoung.wishboard.config.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.hyeeyoung.wishboard.presentation.intro.IntroScreen
import com.hyeeyoung.wishboard.presentation.main.MainScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInVerificationCodeScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignMainScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpPasswordScreen

@Composable
fun WishBoardNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(modifier = modifier, navController = navController, startDestination = Navigation.INTRO.name) {
        composable(Navigation.INTRO.name) {
            IntroScreen(onNavigateToNext = { isLogin ->
                val nextScreen = if (isLogin) Navigation.MAIN.name else Navigation.SIGN.name
                navController.navigate(nextScreen) { popUpTo(navController.graph.id) { inclusive = true } }
            })
        }

        // Sign
        navigation(startDestination = Navigation.SIGN_MAIN.name, route = Navigation.SIGN.name) {
            composable(Navigation.SIGN_MAIN.name) {
                SignMainScreen(
                    onNavigateToSignIn = { navController.navigate(Navigation.SIGN_IN.name) },
                    onNavigateToSignUp = { navController.navigate(Navigation.SIGN_UP.name) },
                )
            }

            navigation(startDestination = Navigation.EMAIL.name, route = Navigation.SIGN_UP.name) {
                composable(Navigation.EMAIL.name) {
                    SignUpEmailScreen(
                        onClickBack = { navController.popBackStack() },
                        onNavigateToNext = { navController.navigate(Navigation.PASSWORD.name) },
                    )
                }
                composable(Navigation.PASSWORD.name) {
                    SignUpPasswordScreen(
                        onClickBack = { navController.popBackStack() },
                        onNavigateToMain = {
                            navController.navigate(Navigation.MAIN.name) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        },
                    )
                }
            }

            composable(Navigation.SIGN_IN.name) {
                SignInScreen(
                    onClickBack = { navController.popBackStack() },
                    onNavigateToMain = {
                        navController.navigate(Navigation.MAIN.name) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToSignInEmail = { navController.navigate(Navigation.SIGN_IN_EMAIL.name) },
                )
            }

            navigation(startDestination = Navigation.EMAIL.name, route = Navigation.SIGN_IN_EMAIL.name) {
                composable(Navigation.EMAIL.name) {
                    SignInEmailScreen(
                        onClickBack = { navController.popBackStack() },
                        onNavigateToNext = { navController.navigate(Navigation.PASSWORD.name) },
                    )
                }
                composable(Navigation.VERIFICATION.name) {
                    SignInVerificationCodeScreen(
                        onClickBack = { navController.popBackStack() },
                        onNavigateToMain = {
                            navController.navigate(Navigation.MAIN.name) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        },
                    )
                }
            }
        }

        // Main
        composable("main") {
            MainScreen()
        }
    }
}
