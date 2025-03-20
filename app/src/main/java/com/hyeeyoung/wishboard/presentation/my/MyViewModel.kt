package com.hyeeyoung.wishboard.presentation.my

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.domain.usecase.auth.PostLogoutUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.wish.model.WishListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val postLogoutUseCase: PostLogoutUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(WishListUiModel())
    val uiModel = _uiModel.asStateFlow()

    fun logout(afterSuccess: () -> Unit) {
        viewModelScope.launch {
            postLogoutUseCase().onSuccess {
                afterSuccess()
            }.onFailure {
                updateSnackbarMessage(SnackbarMessage.DEFAULT)
            }
        }
    }
}