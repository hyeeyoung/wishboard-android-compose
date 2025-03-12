package com.hyeeyoung.wishboard.presentation.wish.screen

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.usecase.item.GetWishItemDetailUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.wish.model.WishItemDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    private val getWishItemUseCase: GetWishItemDetailUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishItemDetailUiModel())
    val uiModel = _uiModel.asStateFlow()

    fun getWishItemDetail(id: Long) {
        viewModelScope.launch {
            getWishItemUseCase(id).onSuccess {
                val detail = it.firstOrNull() ?: return@launch
                _uiModel.update {
                    WishItemDetailUiModel.fromDomain(detail)
                }
            }.onFailure { _, _, _ ->
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }
}