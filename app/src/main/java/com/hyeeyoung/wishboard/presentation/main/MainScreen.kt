package com.hyeeyoung.wishboard.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.config.navigation.navhost.BottomBarNavHost
import com.hyeeyoung.wishboard.designsystem.component.bottombar.WishBoardBottomBar

@Composable
fun MainScreen(wishNavController: NavHostController, onClickAdd: () -> Unit = {}) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setNavigationBarColor(color = Color.White)
    }

    val bottomBarNavController = rememberNavController()
    Scaffold(bottomBar = {
        WishBoardBottomBar(navController = bottomBarNavController, onClickAdd = onClickAdd)
    }) { paddingValues ->
        val modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        BottomBarNavHost(
            modifier = modifier,
            bottomNavController = bottomBarNavController,
            wishNavController = wishNavController,
        )
    }
}

@Preview
@Composable
fun PreviewWishBoardBottomBar() {
    WishBoardBottomBar()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen(wishNavController = rememberNavController())
}
