package com.hyeeyoung.wishboard.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.config.navigation.navgraph.BottomBarNavHost
import com.hyeeyoung.wishboard.designsystem.component.bottombar.WishBoardBottomBar
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        WishBoardBottomBar(navController = navController)
    }) { paddingValues ->
        val modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        BottomBarNavHost(modifier = modifier, navController = navController)
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
    WishboardTheme {
        MainScreen()
    }
}
