package com.hyeeyoung.wishboard.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.config.navigation.navhost.WishBoardNavHost
import com.hyeeyoung.wishboard.designsystem.component.LocalSnackbarHostState
import com.hyeeyoung.wishboard.designsystem.component.WishBoardSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.model.snackbar.WishBoardSnackbarVisuals
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WishboardTheme {
                CompositionLocalProvider(LocalSnackbarHostState provides wishBoardSnackbarHostState) {
                    val systemUiController = rememberSystemUiController()
                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = Color.White,
                            darkIcons = true,
                        )
                    }

                    WishBoardSnackbarMessage(viewModel.globalSnackbarChannel)
                    WishBoardNavHost(navController = rememberNavController())
                }
            }
        }
    }

    fun sendSnackbarVisualChannel(snackbarVisuals: WishBoardSnackbarVisuals) {
        viewModel.sendSnackbarChannel(snackbarVisuals)
    }

    companion object {
        var wishBoardSnackbarHostState: SnackbarHostState = SnackbarHostState()
    }
}
