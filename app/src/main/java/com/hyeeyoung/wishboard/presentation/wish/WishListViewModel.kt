package com.hyeeyoung.wishboard.presentation.wish

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishItemCountUseCase
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishListUseCase
import com.hyeeyoung.wishboard.domain.util.safeValueOf
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardEventBus
import com.hyeeyoung.wishboard.presentation.wish.model.WishListUiModel
import com.hyeeyoung.wishboard.presentation.wish.model.WishListViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val localStorage: WishBoardPreference,
    getWishListUseCase: GetWishListUseCase,
    private val getWishItemCountUseCase: GetWishItemCountUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishListUiModel())
    val uiModel = _uiModel.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val wishList: Flow<PagingData<WishItem>> = _uiModel
        .map { it.isExcludeOwnedItems }
        .distinctUntilChanged()
        .flatMapLatest { isExclude ->
            getWishListUseCase(
                itemStatus = if (isExclude) WishItemOwnershipStatus.WISH else null,
            )
        }
        .cachedIn(viewModelScope)
        .catch { exception ->
            updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    private val _refreshWishListTrigger = Channel<Unit>()
    val refreshWishListTrigger = _refreshWishListTrigger.receiveAsFlow()

    private val _scrollToTopTrigger = Channel<Unit>()
    val scrollToTopTrigger = _scrollToTopTrigger.receiveAsFlow()

    init {
        initUiModel()
        refreshWishList()
        fetchWishItemCount()
    }

    private fun refreshWishList() {
        viewModelScope.launch {
            WishBoardEventBus.onWishItemChanged.collect {
                _refreshWishListTrigger.send(Unit)
            }
        }
    }

    private fun initUiModel() {
        _uiModel.update {
            it.copy(
                shouldShowOnboardingModal = localStorage.shouldShowOnboardingModal,
                viewType = safeValueOf<WishListViewType>(localStorage.wishListViewType)
                    ?: WishListViewType.GRID_2_COLUMN,
                isBulkRegisterBannerVisible = !localStorage.isBulkRegisterBannerDismissed,
            )
        }
    }

    fun dismissBulkRegisterBanner() {
        localStorage.isBulkRegisterBannerDismissed = true
        _uiModel.update { it.copy(isBulkRegisterBannerVisible = false) }
    }

    fun updateOnboardingModalStatus(isOnboardingComplete: Boolean) {
        if (isOnboardingComplete) {
            localStorage.clear(WishBoardPreference.SHOULD_SHOW_ONBOARDING_MODAL)
        }

        _uiModel.update {
            it.copy(shouldShowOnboardingModal = false)
        }
    }

    fun updateViewType() {
        val newViewType = uiModel.value.viewType.next()
        _uiModel.update {
            it.copy(viewType = newViewType)
        }
        localStorage.wishListViewType = newViewType.name
    }

    fun fetchWishItemCount() {
        viewModelScope.launch {
            getWishItemCountUseCase().onSuccess { count ->
                _uiModel.update {
                    it.copy(wishItemCount = count)
                }
            }
        }
    }

    fun updateExcludeOwnedItems(isExclude: Boolean) {
        _uiModel.update { it.copy(isExcludeOwnedItems = isExclude) }
        viewModelScope.launch { _scrollToTopTrigger.send(Unit) }
    }
}
