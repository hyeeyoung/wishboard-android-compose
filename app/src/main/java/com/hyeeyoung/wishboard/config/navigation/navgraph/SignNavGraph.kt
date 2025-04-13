package com.hyeeyoung.wishboard.config.navigation.navgraph

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.hyeeyoung.wishboard.config.navigation.navhost.snackbarComposable
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignInVerificationCodeScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignMainScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpEmailScreen
import com.hyeeyoung.wishboard.presentation.sign.screen.SignUpPasswordScreen

fun NavGraphBuilder.signNavGraph(navController: NavHostController, snackbarHostState: SnackbarHostState) =
    navigation(startDestination = SignScreen.Main.route, route = SignScreen.Root.route) {
        snackbarComposable(snackbarHostState = snackbarHostState, route = SignScreen.Main.route) {
            SignMainScreen(navController)
        }

        navigation(startDestination = SignScreen.Email.route, route = SignScreen.SignUp.route) {
            snackbarComposable(snackbarHostState = snackbarHostState, route = SignScreen.Email.route) {
                SignUpEmailScreen(navController)
            }
            snackbarComposable(snackbarHostState = snackbarHostState, route = SignScreen.Password.route) {
                SignUpPasswordScreen(navController)
            }
        }

        snackbarComposable(snackbarHostState = snackbarHostState, route = SignScreen.Login.route) {
            SignInScreen(navController)
        }

        navigation(startDestination = SignScreen.Email.route, route = SignScreen.EmailLogin.route) {
            snackbarComposable(snackbarHostState = snackbarHostState, route = SignScreen.Email.route) {
                SignInEmailScreen(navController)
            }
            snackbarComposable(snackbarHostState = snackbarHostState, route = SignScreen.Verification.route) {
                SignInVerificationCodeScreen(navController)
            }
        }
    }
