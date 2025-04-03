package com.hyeeyoung.wishboard.presentation.intro

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.usecase.system.GetAppVersionUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val localStorage: WishBoardPreference,
    private val getAppVersionUseCase: GetAppVersionUseCase,
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
        _uiModel.update {
            it.copy(hasShownNotificationAlert = true)
        }
    }

    fun checkForAppUpdate(
        playStoreVersionCode: Int,
        moveToNext: () -> Unit,
        showUpdateDialog: (appUpdateType: AppUpdateTime) -> Unit
    ) {
        if (BuildConfig.VERSION_CODE >= playStoreVersionCode) {
            moveToNext()
            return
        }

        viewModelScope.launch {
            getAppVersionUseCase().onSuccess { remoteVersion ->
                val updateType = if (BuildConfig.VERSION_CODE < remoteVersion.minVersionCode)
                    AppUpdateTime.FORCED_UPDATE else
                    AppUpdateTime.OPTIONAL_UPDATE
                showUpdateDialog(updateType)
            }.onFailure {
                moveToNext()
            }
        }
    }
}
