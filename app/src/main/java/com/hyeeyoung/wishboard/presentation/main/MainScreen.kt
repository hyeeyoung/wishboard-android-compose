package com.hyeeyoung.wishboard.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.util.extension.noRippleClickable

@Composable
fun MainScreen() {
    var selectedMenu by remember { mutableStateOf(BottomNavItem.WishList) }

    Scaffold(bottomBar = {
        WishBoardBottomBar(
            selectedMenu = selectedMenu,
            onSelect = { selected -> selectedMenu = selected },
        )
    }) { paddingValues -> // TODO paddingValues 사용하기
        when (selectedMenu) {
            BottomNavItem.WishList -> {}
            BottomNavItem.Folder -> {}
            BottomNavItem.Add -> {}
            BottomNavItem.Notice -> {}
            BottomNavItem.My -> {}
        }
    }
}

@Composable
fun WishBoardBottomBar(selectedMenu: BottomNavItem, onSelect: (BottomNavItem) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentColor = WishBoardTheme.colors.white,
    ) {
        Column {
            WishBoardDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BottomNavItem.values().forEach { navItem ->
                    BottomBarIconButton(
                        navItem = navItem,
                        isSelected = selectedMenu == navItem,
                        onSelect = onSelect,
                    )
                }
            }
        }
    }
}

@Composable
fun BottomBarIconButton(navItem: BottomNavItem, isSelected: Boolean, onSelect: (BottomNavItem) -> Unit) = Column(
    modifier = Modifier
        .fillMaxHeight()
        .noRippleClickable { onSelect(navItem) },
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
) {
    val buttonColor = if (isSelected) WishBoardTheme.colors.gray700 else WishBoardTheme.colors.gray150
    Icon(painter = painterResource(id = navItem.icon), contentDescription = null, tint = buttonColor)
    Spacer(modifier = Modifier.size(6.dp))
    Text(text = stringResource(id = navItem.label), style = WishBoardTheme.typography.montserratD1, color = buttonColor)
}

enum class BottomNavItem(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    WishList(R.string.nav_menu_label_wishlist, R.drawable.ic_nav_wish_list),
    Folder(R.string.nav_menu_label_folder, R.drawable.ic_nav_folder),
    Add(R.string.nav_menu_label_add, R.drawable.ic_nav_add),
    Notice(R.string.nav_menu_label_notice, R.drawable.ic_nav_notice),
    My(R.string.nav_menu_label_my, R.drawable.ic_nav_my),
}

@Preview
@Composable
fun PreviewWishBoardBottomBar() {
    WishBoardBottomBar(BottomNavItem.WishList, {})
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WishboardTheme {
        MainScreen()
    }
}
