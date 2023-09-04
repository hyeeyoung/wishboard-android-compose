package com.hyeeyoung.wishboard.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.config.navigation.navhost.BottomBarNavHost
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.bottombar.WishBoardBottomBar
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        if (isVisibleBottomBar(navController)) WishBoardBottomBar(navController = navController)
    }) { paddingValues ->
        val modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        BottomBarNavHost(modifier = modifier, navController = navController)
    }
}

@Composable
fun isVisibleBottomBar(navController: NavHostController): Boolean {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return when (navBackStackEntry?.destination?.route) {
        MainScreen.Wishlist.route,
        MainScreen.Folder.makeStartRoute(),
        MainScreen.Add.route, MainScreen.Noti.route,
        MainScreen.My.makeStartRoute(),
        MainScreen.FolderDetail.routeWithArg,
        -> true
        else -> false
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
