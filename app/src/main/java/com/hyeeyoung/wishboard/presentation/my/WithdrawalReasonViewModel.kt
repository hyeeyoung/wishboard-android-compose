package com.hyeeyoung.wishboard.presentation.my

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.user.WithdrawalReason
import com.hyeeyoung.wishboard.domain.usecase.user.DeleteUserAccountUseCase
import com.hyeeyoung.wishboard.domain.usecase.user.GetWithdrawalReasonsUseCase
import com.hyeeyoung.wishboard.domain.usecase.user.SubmitWithdrawalFeedbackUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WithdrawalReasonUiState(
    val reasons: List<WithdrawalReason> = emptyList(),
    val fetchReasonsState: WishBoardState<Unit> = WishBoardState.Loading,
    val selectedReason: WithdrawalReason? = null,
    val etcText: String = "",
    val deleteAccountState: WishBoardState<Unit> = WishBoardState.Idle,
) {
    val isButtonEnabled: Boolean
        get() {
            val reason = selectedReason ?: return false
            return if (reason.detailRequired) etcText.isNotBlank() else true
        }
}

@HiltViewModel
class WithdrawalReasonViewModel @Inject constructor(
    private val getWithdrawalReasonsUseCase: GetWithdrawalReasonsUseCase,
    private val submitWithdrawalFeedbackUseCase: SubmitWithdrawalFeedbackUseCase,
    private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(WithdrawalReasonUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchWithdrawalReasons()
    }

    private fun fetchWithdrawalReasons() {
        viewModelScope.launch {
            getWithdrawalReasonsUseCase().onSuccess { reasons ->
                _uiState.update { it.copy(reasons = reasons, fetchReasonsState = WishBoardState.Success(Unit)) }
            }.onFailure { exception, _, _ ->
                _uiState.update { it.copy(fetchReasonsState = WishBoardState.Failure) }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun onSelectReason(reason: WithdrawalReason) {
        _uiState.update { it.copy(selectedReason = reason, etcText = "") }
    }

    fun onEtcTextChange(text: String) {
        if (text.length <= MAX_ETC_LENGTH) {
            _uiState.update { it.copy(etcText = text) }
        }
    }

    fun deleteAccount(afterSuccess: () -> Unit) {
        if (_uiState.value.deleteAccountState is WishBoardState.Loading) return
        val reason = _uiState.value.selectedReason ?: return
        val detail = if (reason.detailRequired) _uiState.value.etcText.trim() else null
        _uiState.update { it.copy(deleteAccountState = WishBoardState.Loading) }
        viewModelScope.launch {
            submitWithdrawalFeedbackUseCase(reasonCode = reason.code, detail = detail).onFailure { exception, _, _ ->
                _uiState.update { it.copy(deleteAccountState = WishBoardState.Failure) }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }.onSuccess {
                deleteAccountAfterFeedback(afterSuccess)
            }
        }
    }

    private suspend fun deleteAccountAfterFeedback(afterSuccess: () -> Unit) {
        deleteUserAccountUseCase().onSuccess {
            // 화면 전환 전까지 버튼 로딩 상태를 유지하기 위해 Success로 갱신하지 않는다.
            afterSuccess()
            updateSnackbarMessage("탈퇴 완료되었어요. 이용해주셔서 감사합니다!☺️")
        }.onFailure { exception, _, _ ->
            _uiState.update { it.copy(deleteAccountState = WishBoardState.Failure) }
            updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
        }
    }

    companion object {
        const val MAX_ETC_LENGTH = 200
    }
}
