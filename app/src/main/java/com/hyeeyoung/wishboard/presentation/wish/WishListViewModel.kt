package com.hyeeyoung.wishboard.presentation.wish

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishListUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import com.hyeeyoung.wishboard.presentation.wish.model.WishListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    getWishListUseCase: GetWishListUseCase,
    private val localStorage: WishBoardPreference,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishListUiModel())
    val uiModel = _uiModel.asStateFlow()

    val wishList = getWishListUseCase()
        .cachedIn(viewModelScope)
        .catch { exception ->
            updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())
    private val _refreshWishListTrigger = Channel<Unit>()
    val refreshWishListTrigger = _refreshWishListTrigger.receiveAsFlow()

    init {
        initOnboardingModalState()
        refreshWishList()
    }

    private fun refreshWishList() {
        viewModelScope.launch {
            WishBoardEventBus.onWishItemChanged.collect {
                _refreshWishListTrigger.send(Unit)
            }
        }
    }

    private fun initOnboardingModalState() {
        _uiModel.update {
            it.copy(shouldShowOnboardingModal = localStorage.shouldShowOnboardingModal)
        }
    }

    fun updateOnboardingModalStatus(isOnboardingComplete: Boolean) {
        if (isOnboardingComplete) {
            localStorage.clear(WishBoardPreference.SHOULD_SHOW_ONBOARDING_MODAL)
        }

        _uiModel.update {
            it.copy(shouldShowOnboardingModal = false)
        }
    }
}
