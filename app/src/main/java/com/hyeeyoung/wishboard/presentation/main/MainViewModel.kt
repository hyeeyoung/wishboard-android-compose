package com.hyeeyoung.wishboard.presentation.main

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.config.GlobalState
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.WishBoardSnackbarVisuals
import com.hyeeyoung.wishboard.presentation.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context,
) : BaseViewModel() {
    /** 스낵바 시각 정보(전역으로 사용) */
    val globalSnackbarChannel = Channel<WishBoardSnackbarVisuals>(
        capacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val isConnectedNetwork = NetworkMonitor(context, coroutineScope = viewModelScope).isConnected

    init {
        checkNetworkStatus()
    }

    fun sendSnackbarChannel(snackbarVisuals: WishBoardSnackbarVisuals) {
        viewModelScope.launch {
            globalSnackbarChannel.send(snackbarVisuals)
        }
    }

    private fun checkNetworkStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            isConnectedNetwork.collectLatest { isConnected ->
                when (isConnected) {
                    true -> globalSnackbarChannel.send(WishBoardSnackbarVisuals(message = ""))

                    else -> globalSnackbarChannel.send(
                        WishBoardSnackbarVisuals(
                            message = SnackbarMessage.NETWORK_CONNECTION_ERROR,
                            duration = SnackbarDuration.Indefinite,
                        ),
                    )
                }
            }
        }
    }

    fun checkAuthLoginState(moveToStartRoute: () -> Unit) {
        viewModelScope.launch {
            GlobalState.autoLoginExpiryInfo.collectLatest { autoLoginInfo ->
                if (autoLoginInfo.first) {
                    moveToStartRoute()
                    sendSnackbarChannel(
                        WishBoardSnackbarVisuals(
                            message = autoLoginInfo.second,
                        ),
                    )
                    GlobalState.autoLoginExpiryInfo.value = false to ""
                }
            }
        }
    }
}
