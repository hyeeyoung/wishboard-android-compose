package com.hyeeyoung.wishboard.config.navigation.screen

sealed class Sign(override val route: String) : Screen {
    object Root : Sign(route = "signRoot")
    object Main : Sign(route = "signMain")
    object SignUp : Sign(route = "signUp")
    object Login : Sign(route = "login")
    object EmailLogin : Sign(route = "emailLogin")
    object Email : Sign(route = "email")
    object Password : Sign(route = "password")
    object Verification : Sign(route = "verification")
}
