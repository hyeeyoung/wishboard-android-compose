package com.hyeeyoung.wishboard.presentation.noti

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.domain.usecase.noti.GetPreviousNotiListUseCase
import com.hyeeyoung.wishboard.domain.usecase.noti.PutNotiReadStateUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotiViewModel @Inject constructor(
    private val getPreviousNotiListUseCase: GetPreviousNotiListUseCase,
    private val putNotiReadStateUseCase: PutNotiReadStateUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(NotiListUiModel())
    val uiModel = _uiModel.asStateFlow()

    fun fetchPreviousNoti(didRefresh: Boolean) {
        _uiModel.update { it.copy(isRefreshing = didRefresh) }
        
        viewModelScope.launch {
            getPreviousNotiListUseCase().onSuccess { notiList ->
                _uiModel.update { it.copy(notiList = notiList, isRefreshing = false) }
            }.onFailure {
                _uiModel.update { it.copy(isRefreshing = false) }
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }

    fun updateReadState(itemId: Long) {
        val targetNoti = uiModel.value.notiList.find { it.itemId == itemId } ?: return
        if (targetNoti.isRead) return

        _uiModel.update {
            it.copy(
                notiList = it.notiList.map { noti ->
                    if (noti.itemId == itemId) { noti.copy(isRead = true) } else noti
                }
            )
        }

        viewModelScope.launch {
            putNotiReadStateUseCase(itemId = itemId)
        }
    }
}
