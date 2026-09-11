package com.hyeeyoung.wishboard.config.navigation.screen

sealed class SignScreen(override val route: String) : Screen {
    data object Root : SignScreen(route = "signRoot")
    data object Main : SignScreen(route = "signMain")
    data object SignUp : SignScreen(route = "signUp")
    data object Login : SignScreen(route = "login")
    data object EmailLogin : SignScreen(route = "emailLogin")
    data object Email : SignScreen(route = "email")
    data object Password : SignScreen(route = "password")
    data object Verification : SignScreen(route = "verification")
}
