package com.hyeeyoung.wishboard.presentation.wish

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishListUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.wish.model.WishListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val getWishListUseCase: GetWishListUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishListUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        getWishItem()
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
                // TODO 아이템 정보 없을 때 어떤 에러코드로 내려오는지 확인. 문서 상 404로 떨어진다고 함
                _uiModel.update {
                    it.copy(fetchState = WishBoardState.Failure, isRefreshing = false)
                }
            }
        }
    }
}