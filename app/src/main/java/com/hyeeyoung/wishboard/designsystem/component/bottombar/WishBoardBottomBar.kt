package com.hyeeyoung.wishboard.designsystem.component.bottombar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun WishBoardBottomBar(navController: NavHostController = rememberNavController(), onClickAdd: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Column {
            WishBoardDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WishBoardTheme.colors.white)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BottomNavItem.values().forEach { navItem ->
                    BottomBarIconButton(
                        navItem = navItem,
                        isSelected = isSelectedMenu(currentRoute = currentRoute, navItem = navItem),
                        onSelect = {
                            if (navItem.screen.route == BottomNavItem.Add.screen.route) {
                                onClickAdd()
                            } else {
                                navController.navigate(route = navItem.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

fun isSelectedMenu(currentRoute: String?, navItem: BottomNavItem): Boolean {
    return when (navItem) {
        BottomNavItem.Folder ->
            currentRoute in listOf(MainScreen.Folder.getStartRouteForMainTab(), MainScreen.FolderDetail.routeWithArg)

        else ->
            currentRoute == navItem.screen.getStartRouteForMainTab()
    }
}

@Composable
fun BottomBarIconButton(navItem: BottomNavItem, isSelected: Boolean, onSelect: (String) -> Unit) = Column(
    modifier = Modifier
        .fillMaxHeight()
        .noRippleClickable { onSelect(navItem.screen.route) },
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
) {
    val btnColor = if (isSelected) WishBoardTheme.colors.gray700 else WishBoardTheme.colors.gray150
    Icon(painter = painterResource(id = navItem.icon), contentDescription = null, tint = btnColor)
    Spacer(modifier = Modifier.size(6.dp))
    Text(text = stringResource(id = navItem.label), style = WishBoardTheme.typography.montserratD1, color = btnColor)
}

enum class BottomNavItem(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    val screen: MainScreen,
) {
    WishList(R.string.nav_menu_label_wishlist, R.drawable.ic_nav_wish_list, MainScreen.Wishlist),
    Folder(R.string.nav_menu_label_folder, R.drawable.ic_nav_folder, MainScreen.Folder),
    Add(R.string.nav_menu_label_add, R.drawable.ic_nav_write, MainScreen.Upload),
    Notice(R.string.nav_menu_label_notice, R.drawable.ic_nav_notice, MainScreen.Noti),
    My(R.string.nav_menu_label_my, R.drawable.ic_nav_my, MainScreen.My),
}
