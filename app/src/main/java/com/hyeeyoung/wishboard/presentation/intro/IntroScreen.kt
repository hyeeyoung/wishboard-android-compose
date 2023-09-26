package com.hyeeyoung.wishboard.presentation.intro

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.Intro
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.designsystem.component.dialog.WishBoardDialog
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.dialog.DialogData
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(navController: NavHostController) {
    val context = LocalContext.current
    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    LaunchedEffect(Unit) {
        delay(2000L)
        checkForNewVersionUpdate(
            context = context,
            showDialog = { dialogData = DialogData.Intro },
            moveToNext = { navigateToNext(navController) },
        )
    }

    WishboardTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WishBoardTheme.colors.white),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(painter = painterResource(id = R.drawable.ic_app_text_logo), contentDescription = null)
            Spacer(modifier = Modifier.size(10.dp))
        }

        WishBoardDialog(
            dialogData = dialogData,
            onClickConfirm = {},
            onDismissRequest = { dialogData = null },
        )
    }
}

private fun checkForNewVersionUpdate(context: Context, showDialog: () -> Unit, moveToNext: () -> Unit) {
    val appUpdateManager = AppUpdateManagerFactory.create(context)
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) &&
            appUpdateInfo.availableVersionCode() != BuildConfig.VERSION_CODE
        ) {
            showDialog()
        } else {
            moveToNext()
        }
    }.addOnFailureListener {
        moveToNext()
    }
}

fun navigateToNext(navController: NavHostController) {
    val isLogin = false // TODO 로컬 디비에서 로그인 여부 가져오기
    val nextScreen = if (isLogin) MainScreen.Root.route else SignScreen.Root.route
    navController.navigate(nextScreen) { popUpTo(Intro.route) { inclusive = true } }
}

@Preview
@Composable
fun PreviewIntroScreen() {
    IntroScreen(rememberNavController())
}
