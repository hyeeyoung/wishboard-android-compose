package com.hyeeyoung.wishboard.config.navigation.screen

sealed class Main(override val route: String) : Screen {
    object Root : Main(route = "mainRoot")
    object Home : Main(route = "home")
    object Folder : Main(route = "folder")
    object Add : Main(route = "add")
    object Noti : Main(route = "noti")
    object My : Main(route = "my")
}
