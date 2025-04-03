package com.hyeeyoung.wishboard.presentation.intro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardOneButtonDialog
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardTwoButtonDialog
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun IntroScreen(
    navController: NavController,
    viewModel: IntroViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            viewModel.updateNotificationAlertDate()
        }
    var nextScreen by remember { mutableStateOf<String?>(null) }
    var appUpdateType by remember { mutableStateOf<AppUpdateTime?>(null) }

    LaunchedEffect(uiModel.hasShownNotificationAlert) {
        Timber.e("hasShownNotificationAlert : ${uiModel.hasShownNotificationAlert}")
        if (uiModel.hasShownNotificationAlert == false) {
            checkNotificationPermission(
                context = context,
                requestPermissionLauncher = requestPermissionLauncher,
                shouldSkip = {
                    viewModel.updateNotificationAlertDate()
                })
        }
    }

    LaunchedEffect(uiModel.isLogin) {
        if (uiModel.isLogin == null) return@LaunchedEffect
        delay(2000L)
        Timber.e("isLogin : ${uiModel.isLogin}")

        checkForNewVersionUpdate(
            context = context,
            checkRemoteAppVersion = { playStoreVersionCode ->
                viewModel.checkForAppUpdate(
                    playStoreVersionCode = playStoreVersionCode,
                    moveToNext = {
                        nextScreen = getNextScreen(uiModel.isLogin!!)
                    },
                    showUpdateDialog = {
                        appUpdateType = it
                    }
                )
            },
            moveToNext = {
                nextScreen = getNextScreen(uiModel.isLogin!!)
            },
        )
    }

    LaunchedEffect(nextScreen, uiModel.hasShownNotificationAlert) {
        Timber.e("nextScreen : $nextScreen, hasShownNotificationAlert : ${uiModel.hasShownNotificationAlert}")
        if (nextScreen == null || uiModel.hasShownNotificationAlert != true) return@LaunchedEffect
        navController.navigate(nextScreen!!) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

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

    if (appUpdateType == AppUpdateTime.OPTIONAL_UPDATE && uiModel.hasShownNotificationAlert == true) {
        WishBoardTwoButtonDialog(
            dialogData = DialogData.AppUpdate,
            onClickConfirm = {
                moveToPlayStore(context)
            },
            onDismissRequest = {
                dialogData = null
                nextScreen = getNextScreen(uiModel.isLogin!!)
            },
        )
    }

    if (appUpdateType == AppUpdateTime.FORCED_UPDATE && uiModel.hasShownNotificationAlert == true) {
        WishBoardOneButtonDialog(
            dialogData = DialogData.AppUpdate,
            onClickConfirm = {
                moveToPlayStore(context)
            },
            onDismissRequest = {},
        )
    }
}

private fun getNextScreen(isLogin: Boolean): String =
    if (isLogin) "${MainScreen.Root.route}/${false}" else SignScreen.Root.route

private fun moveToPlayStore(context: Context) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("${context.getString(R.string.play_store_detail_url)}${context.packageName}")
    )
    context.startActivity(intent)
}

private fun checkForNewVersionUpdate(
    context: Context,
    checkRemoteAppVersion: (playStoreVersionCode: Int) -> Unit,
    moveToNext: () -> Unit
) {
    val appUpdateManager = AppUpdateManagerFactory.create(context)
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        val playStoreVersionCode = appUpdateInfo.availableVersionCode()
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) &&
            playStoreVersionCode != BuildConfig.VERSION_CODE
        ) {
            Timber.e("1 : $playStoreVersionCode")
            checkRemoteAppVersion(playStoreVersionCode)
        } else {
            Timber.e("2")
            moveToNext()
        }
    }.addOnFailureListener {
        Timber.e("3")
        moveToNext()
    }
}

private fun checkNotificationPermission(
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    shouldSkip: () -> Unit,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        shouldSkip()
        return
    }
    val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    when (isGranted) {
        false -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

        true -> {}
    }
}

@Preview
@Composable
fun PreviewIntroScreen() {
    IntroScreen(rememberNavController())
}
