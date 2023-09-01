package com.hyeeyoung.wishboard.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardThickDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardMainTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.MyMenuComponent
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun MyScreen() {
    // TODO 알림 토글 및 클릭 이벤트 핸들링
    val myMenuComponents =
        listOf(
            MyMenuComponent.Divider,
            MyMenuComponent.Menu(nameRes = R.string.my_menu_push_setting, endComponent = { /*TODO*/ }),
            MyMenuComponent.Menu(nameRes = R.string.my_menu_change_password, onClickMenu = {}),
            MyMenuComponent.Divider,
            MyMenuComponent.Menu(nameRes = R.string.my_menu_inquiry, onClickMenu = {}),
            MyMenuComponent.Menu(nameRes = R.string.my_menu_manual, onClickMenu = {}),
            MyMenuComponent.Menu(nameRes = R.string.my_menu_terms, onClickMenu = {}),
            MyMenuComponent.Menu(nameRes = R.string.my_menu_privacy, onClickMenu = {}),
            MyMenuComponent.Menu(nameRes = R.string.my_menu_open_source, onClickMenu = {}),
            MyMenuComponent.Menu(
                nameRes = R.string.my_menu_version,
                endComponent = {
                    Text(
                        text = BuildConfig.VERSION_NAME,
                        style = WishBoardTheme.typography.montserratB1,
                        color = WishBoardTheme.colors.gray300,
                    )
                },
            ),
            MyMenuComponent.Divider,
            MyMenuComponent.Menu(nameRes = R.string.my_menu_logout, onClickMenu = {}),
            MyMenuComponent.Menu(nameRes = R.string.my_menu_withdraw, onClickMenu = {}),
        )

    Scaffold(topBar = {
        WishBoardMainTopBar(titleRes = R.string.my)
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(myMenuComponents) { menuComponent ->
                    when (menuComponent) {
                        is MyMenuComponent.Menu -> MenuItem(menu = menuComponent)
                        is MyMenuComponent.Divider -> WishBoardThickDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItem(menu: MyMenuComponent.Menu) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { menu.onClickMenu?.let { onclick -> onclick() } }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(id = menu.nameRes),
            style = WishBoardTheme.typography.suitD1,
            color = WishBoardTheme.colors.gray600,
        )
        menu.endComponent?.let { endComponent -> endComponent() }
    }
}

@Composable
@Preview
fun PreviewMyScreen() {
    MyScreen()
}

@Preview(showBackground = true)
@Composable
fun PreviewMyMenuItem() {
    MenuItem(
        menu = MyMenuComponent.Menu(nameRes = R.string.my_menu_push_setting, endComponent = {}),
    )
}