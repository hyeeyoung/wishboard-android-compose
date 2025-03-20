package com.hyeeyoung.wishboard.presentation.common

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.config.GlobalState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.WishBoardSnackbarVisuals
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    val snackBarChannel = Channel<WishBoardSnackbarVisuals>(
        capacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun updateSnackbarMessage(
        message: String,
        vibrate: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        viewModelScope.launch {
            snackBarChannel.send(
                WishBoardSnackbarVisuals(
                    message = message,
                    vibrate = vibrate,
                    duration = duration,
                ),
            )
        }
    }

    fun checkAuthLoginState(moveToStartRoute: () -> Unit) {
        viewModelScope.launch {
            GlobalState.isExpiredAuthLogin.collectLatest { isExpired ->
                if (isExpired) {
                    updateSnackbarMessage(SnackbarMessage.AUTO_LOGIN)
                    GlobalState.isExpiredAuthLogin.value = false
                    moveToStartRoute()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        snackBarChannel.close()
    }
}
