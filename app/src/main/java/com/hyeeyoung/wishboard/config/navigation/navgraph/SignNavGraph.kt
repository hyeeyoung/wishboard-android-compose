package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
            SignMainScreen(navController)
        }

        navigation(startDestination = Sign.Email.route, route = Sign.SignUp.route) {
            composable(Sign.Email.route) {
                SignUpEmailScreen(navController)
            }
            composable(Sign.Password.route) {
                SignUpPasswordScreen(navController)
            }
        }

        composable(Sign.Login.route) {
            SignInScreen(navController)
        }

        navigation(startDestination = Sign.Email.route, route = Sign.EmailLogin.route) {
            composable(Sign.Email.route) {
                SignInEmailScreen(navController)
            }
            composable(Sign.Verification.route) {
                SignInVerificationCodeScreen(navController)
            }
        }
    }
