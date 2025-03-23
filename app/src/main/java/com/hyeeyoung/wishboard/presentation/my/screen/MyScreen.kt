package com.hyeeyoung.wishboard.presentation.my.screen

import android.os.Build
import androidx.annotation.StringRes
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardToggleButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardMiniButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardDialog
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardThickDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardMainTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.presentation.my.model.MyUiModel
import com.hyeeyoung.wishboard.presentation.util.constant.WishBoardUrl
import com.hyeeyoung.wishboard.presentation.util.extension.moveToWebView
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.sendMail

@Composable
fun MyScreen(
    navController: NavHostController,
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchUserInfo()
    }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    MyScreen(
        uiModel = uiModel,
        navigate = { route ->
            navController.navigate(route)
        },
        updatePushState = viewModel::updatePushState,
        logout = {
            viewModel.logout {
                navController.navigate(SignScreen.Main.route) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }
        },
        deleteAccount = {
            viewModel.deleteAccount {
                navController.navigate(SignScreen.Main.route) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }
        },
        moveToWebView = { title, url ->
            navController.moveToWebView(title = title, url = url)
        }
    )
}

@Composable
fun MyScreen(
    uiModel: MyUiModel,
    navigate: (route: String) -> Unit,
    updatePushState: (Boolean) -> Unit,
    logout: () -> Unit,
    deleteAccount: () -> Unit,
    moveToWebView: (title: String?, url: String) -> Unit,
) {
    val context = LocalContext.current
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    val withdrawalEmailInput = remember { mutableStateOf("") }
    val isEnableWithdrawal by remember(withdrawalEmailInput.value, uiModel.userInfo.email) {
        mutableStateOf(
            withdrawalEmailInput.value.isNotBlank()
                    && withdrawalEmailInput.value.trimEnd() == uiModel.userInfo.email
        )
    }

    val myMenuComponents =
        listOf(
            MyMenuComponent.Divider,
            MyMenuComponent.Menu(nameRes = R.string.my_menu_push_setting, endComponent = {
                WishBoardToggleButton(
                    selected = uiModel.userInfo.isPushAllowed ?: false,
                    onUpdate = updatePushState
                )
            }),
            MyMenuComponent.Menu(nameRes = R.string.my_menu_change_password, onClickMenu = {
                navigate(MainScreen.MyPasswordChange.route)
            }),
            MyMenuComponent.Divider,
            MyMenuComponent.Menu(nameRes = R.string.my_menu_contact_us, onClickMenu = {
                with(context) {
                    sendMail(
                        getString(R.string.my_contact_us_email_title),
                        String.format(
                            getString(R.string.my_contact_us_email_content),
                            Build.BRAND,
                            Build.MODEL,
                            BuildConfig.VERSION_NAME,
                            Build.VERSION.SDK_INT,
                        ),
                    )
                }
            }),
            MyMenuComponent.Menu(nameRes = R.string.my_menu_manual, onClickMenu = {
                moveToWebView(WishBoardUrl.HOW_TO_USE.title, WishBoardUrl.HOW_TO_USE.url)
            }),
            MyMenuComponent.Menu(
                nameRes = R.string.my_menu_terms,
                onClickMenu = {
                    moveToWebView(WishBoardUrl.TERMS.title, WishBoardUrl.TERMS.url)
                },
            ),
            MyMenuComponent.Menu(
                nameRes = R.string.my_menu_privacy,
                onClickMenu = {
                    moveToWebView(
                        WishBoardUrl.PRIVACY_POLICY.title,
                        WishBoardUrl.PRIVACY_POLICY.url
                    )
                },
            ),
            MyMenuComponent.Menu(
                nameRes = R.string.my_menu_open_source,
                onClickMenu = {
                    moveToWebView(WishBoardUrl.OPEN_SOURCE.title, WishBoardUrl.OPEN_SOURCE.url)
                },
            ),
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
            MyMenuComponent.Menu(
                nameRes = R.string.my_menu_logout,
                onClickMenu = { dialogData = DialogData.Logout },
            ),
            MyMenuComponent.Menu(
                nameRes = R.string.my_menu_withdraw,
                onClickMenu = {
                    withdrawalEmailInput.value = ""
                    dialogData = DialogData.Withdraw
                },
            ),
        )

    Scaffold(topBar = {
        WishBoardMainTopBar(titleRes = R.string.my)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            item { Profile(userInfo = uiModel.userInfo, onClickProfileEdit = { navigate(MainScreen.MyProfile.route) }) }

            items(myMenuComponents) { menuComponent ->
                when (menuComponent) {
                    is MyMenuComponent.Menu -> MenuItem(menu = menuComponent)
                    is MyMenuComponent.Divider -> WishBoardThickDivider()
                }
            }

            item { Spacer(modifier = Modifier.size(64.dp)) }
        }

        WishBoardDialog(
            dialogData = dialogData,
            onClickConfirm = {
                when (dialogData) {
                    DialogData.Logout -> logout()

                    DialogData.Withdraw -> {
                        if (isEnableWithdrawal) {
                            deleteAccount()
                        }
                    }

                    else -> {}
                }
            },
            onDismissRequest = {
                dialogData = null
            },
            dismissOnConfirm = !(dialogData is DialogData.Withdraw && !isEnableWithdrawal),
            content = if (dialogData is DialogData.Withdraw) {
                { WithdrawDialogContent(emailInput = withdrawalEmailInput, isEnableWithdrawal = isEnableWithdrawal) }
            } else {
                null
            },
        )
    }
}

@Composable
fun Profile(userInfo: UserInfo, onClickProfileEdit: () -> Unit) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 34.dp, bottom = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            model = userInfo.profileImage,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            placeHolder = {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_placeholder_user_profile),
                    contentDescription = "",
                )
            }
        )

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = userInfo.nickname,
                style = WishBoardTheme.typography.suitH2,
                color = WishBoardTheme.colors.gray700,
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = userInfo.email,
                style = WishBoardTheme.typography.suitB3,
                color = WishBoardTheme.colors.gray200,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        WishBoardMiniButton(
            onClick = { onClickProfileEdit() },
            text = stringResource(id = R.string.edit),
        )
    }
}

@Composable
fun WithdrawDialogContent(emailInput: MutableState<String>, isEnableWithdrawal: Boolean) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 14.dp),
    ) {
        WishBoardTextField(
            input = emailInput,
            isError = emailInput.value.isNotBlank() && !isEnableWithdrawal,
            placeholder = stringResource(id = R.string.sign_email_placeholder),
            errorMsg = stringResource(id = R.string.dialog_withdraw_email_error)
        )
    }
}

sealed class MyMenuComponent {
    data class Menu(
        @StringRes val nameRes: Int,
        val onClickMenu: (() -> Unit)? = null,
        val endComponent: (@Composable () -> Unit)? = null,
    ) : MyMenuComponent()

    data object Divider : MyMenuComponent()
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
    MyScreen(
        uiModel = MyUiModel(
            userInfo = UserInfo(
                email = "youngjin@naver.com",
                nickname = "새침한 진주",
                isPushAllowed = true
            )
        ),
        navigate = {},
        updatePushState = {},
        logout = {},
        deleteAccount = {},
        moveToWebView = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMyMenuItem() {
    MenuItem(
        menu = MyMenuComponent.Menu(nameRes = R.string.my_menu_push_setting, endComponent = {}),
    )
}
