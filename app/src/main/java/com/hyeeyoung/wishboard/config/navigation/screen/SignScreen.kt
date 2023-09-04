package com.hyeeyoung.wishboard.config.navigation.screen

sealed class SignScreen(override val route: String) : Screen {
    object Root : SignScreen(route = "signRoot")
    object Main : SignScreen(route = "signMain")
    object SignUp : SignScreen(route = "signUp")
    object Login : SignScreen(route = "login")
    object EmailLogin : SignScreen(route = "emailLogin")
    object Email : SignScreen(route = "email")
    object Password : SignScreen(route = "password")
    object Verification : SignScreen(route = "verification")
}
