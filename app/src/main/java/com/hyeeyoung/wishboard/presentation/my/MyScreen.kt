package com.hyeeyoung.wishboard.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardToggleButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardMiniButton
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardThickDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardMainTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.MyMenuComponent
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun MyScreen() {
    // TODO 클릭 이벤트 핸들링
    val myMenuComponents =
        listOf(
            MyMenuComponent.Divider,
            MyMenuComponent.Menu(nameRes = R.string.my_menu_push_setting, endComponent = {
                var isSelected by remember { mutableStateOf(true) }
                WishBoardToggleButton(selected = isSelected, onUpdate = { selected -> isSelected = selected })
            }),
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
        LazyColumn(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            item { Profile() }
            items(myMenuComponents) { menuComponent ->
                when (menuComponent) {
                    is MyMenuComponent.Menu -> MenuItem(menu = menuComponent)
                    is MyMenuComponent.Divider -> WishBoardThickDivider()
                }
            }
            item { Spacer(modifier = Modifier.size(64.dp)) }
        }
    }
}

@Composable
fun Profile() {
    // TODO 서버 연동 후 더미데이터 삭제
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 34.dp, bottom = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ColoredImage(
            model = "https://url.kr/8vwf1e",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
        )

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = "새침한 진주",
                style = WishBoardTheme.typography.suitH2,
                color = WishBoardTheme.colors.gray700,
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "youngjin@naver.com",
                style = WishBoardTheme.typography.suitB3,
                color = WishBoardTheme.colors.gray200,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        WishBoardMiniButton(
            onClick = { /*TODO*/ },
            text = stringResource(id = R.string.edit),
        )
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
        verticalAlignment = Alignment.CenterVertically,
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
