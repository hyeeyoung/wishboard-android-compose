package com.hyeeyoung.wishboard.presentation.noti

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.usecase.noti.GetPreviousNotiListUseCase
import com.hyeeyoung.wishboard.domain.usecase.noti.PutNotiReadStateUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.noti.model.NotiListUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _refreshNotiListTrigger = Channel<Unit>()
    val refreshNotiListTrigger = _refreshNotiListTrigger.receiveAsFlow()

    init {
        fetchPreviousNoti(false)
        refreshNotiList()
    }

    private fun refreshNotiList() {
        viewModelScope.launch {
            WishBoardEventBus.onWishItemChanged.collect {
                _refreshNotiListTrigger.send(Unit)
            }
        }
    }

    fun fetchPreviousNoti(didRefresh: Boolean) {
        _uiModel.update { it.copy(isRefreshing = didRefresh) }

        viewModelScope.launch {
            getPreviousNotiListUseCase().onSuccess { notiList ->
                _uiModel.update { it.copy(notiList = notiList, isRefreshing = false) }
            }.onFailure { exception, _, _ ->
                _uiModel.update { it.copy(isRefreshing = false) }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun updateReadState(itemId: Long) {
        val targetNoti = uiModel.value.notiList.find { it.itemId == itemId } ?: return
        if (targetNoti.isRead) return

        _uiModel.update {
            it.copy(
                notiList = it.notiList.map { noti ->
                    if (noti.itemId == itemId) {
                        noti.copy(isRead = true)
                    } else {
                        noti
                    }
                },
            )
        }

        viewModelScope.launch {
            putNotiReadStateUseCase(itemId = itemId)
        }
    }
}
