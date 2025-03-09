package com.hyeeyoung.wishboard.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.presentation.model.snackbar.WishBoardSnackbarVisuals
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
//    private val isExpiredAutoLogin =
//        getFlowDataStoreUseCase(DataStoreKeys.IS_EXPIRED_AUTO_LOGIN, false)
//            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

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
