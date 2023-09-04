package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInVerificationCodeScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignMainScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpPasswordScreen

fun NavGraphBuilder.signNavGraph(navController: NavHostController) =
    navigation(startDestination = SignScreen.Main.route, route = SignScreen.Root.route) {
        composable(SignScreen.Main.route) {
            SignMainScreen(navController)
        }

        navigation(startDestination = SignScreen.Email.route, route = SignScreen.SignUp.route) {
            composable(SignScreen.Email.route) {
                SignUpEmailScreen(navController)
            }
            composable(SignScreen.Password.route) {
                SignUpPasswordScreen(navController)
            }
        }

        composable(SignScreen.Login.route) {
            SignInScreen(navController)
        }

        navigation(startDestination = SignScreen.Email.route, route = SignScreen.EmailLogin.route) {
            composable(SignScreen.Email.route) {
                SignInEmailScreen(navController)
            }
            composable(SignScreen.Verification.route) {
                SignInVerificationCodeScreen(navController)
            }
        }
    }
