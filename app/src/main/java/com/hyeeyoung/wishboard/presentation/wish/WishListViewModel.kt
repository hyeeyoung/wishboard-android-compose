package com.hyeeyoung.wishboard.presentation.wish

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishListUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.wish.model.WishListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val localStorage: WishBoardPreference,
    private val getWishListUseCase: GetWishListUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishListUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        initOnboardingModalState()
    }

    private fun initOnboardingModalState() {
        _uiModel.update {
            it.copy(shouldShowOnboardingModal = localStorage.shouldShowOnboardingModal)
        }
    }

    fun getWishItem(didRefresh: Boolean = false) {
        if (_uiModel.value.fetchState is WishBoardState.Loading) return
        _uiModel.update {
            it.copy(fetchState = WishBoardState.Loading, isRefreshing = didRefresh)
        }

        viewModelScope.launch {
            getWishListUseCase().onSuccess { items ->
                _uiModel.update {
                    it.copy(withItems = items, fetchState = WishBoardState.Success(Unit), isRefreshing = false)
                }
            }.onFailure { _, errorCode, _ ->
                when (errorCode) {
                    404 -> _uiModel.update {
                        it.copy(withItems = emptyList(), fetchState = WishBoardState.Success(Unit), isRefreshing = false)
                    }

                    else -> {
                        _uiModel.update {
                            it.copy(fetchState = WishBoardState.Failure, isRefreshing = false)
                        }
                        updateSnackbarMessage(SnackbarMessage.DEFAULT)
                    }
                }

            }
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