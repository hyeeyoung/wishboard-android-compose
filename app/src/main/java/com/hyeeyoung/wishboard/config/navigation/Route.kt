package com.hyeeyoung.wishboard.config.navigation

sealed class Route {
    object Intro : Route()
    object Sign : Route() {
        const val main = "signMain"
        const val signUp = "signUp"
        const val login = "login"
        const val emailLogin = "emailLogin"
        const val email = "email"
        const val password = "password"
        const val verification = "verification"
    }
    object Main : Route()

    fun toRoute(): String = this.javaClass.simpleName.lowercase()
}
