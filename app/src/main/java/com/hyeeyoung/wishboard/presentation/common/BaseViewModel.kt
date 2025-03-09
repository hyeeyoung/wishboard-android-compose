package com.hyeeyoung.wishboard.presentation.common

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.presentation.model.snackbar.WishBoardSnackbarVisuals
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
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

    override fun onCleared() {
        super.onCleared()

        snackBarChannel.close()
    }
}
