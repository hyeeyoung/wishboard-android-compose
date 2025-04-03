package com.hyeeyoung.wishboard.presentation.intro

import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val localStorage: WishBoardPreference,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(IntroUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        checkLoginAndNotificationAlerInfo()
    }

    private fun checkLoginAndNotificationAlerInfo() {
        _uiModel.update {
            it.copy(
                isLogin = localStorage.isLogin,
                hasShownNotificationAlert = localStorage.hasShownNotificationAlert
            )
        }
    }

    fun updateNotificationAlertDate() {
        localStorage.hasShownNotificationAlert = true
    }
}
