package com.hyeeyoung.wishboard.presentation.main

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.WishBoardSnackbarVisuals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : BaseViewModel() {
    /** 스낵바 시각 정보(전역으로 사용) */
    val globalSnackbarChannel = Channel<WishBoardSnackbarVisuals>(
        capacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun sendSnackbarChannel(snackbarVisuals: WishBoardSnackbarVisuals) {
        viewModelScope.launch {
            globalSnackbarChannel.send(snackbarVisuals)
        }
    }
}
