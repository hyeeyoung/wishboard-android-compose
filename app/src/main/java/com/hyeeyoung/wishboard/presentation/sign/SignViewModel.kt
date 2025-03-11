package com.hyeeyoung.wishboard.presentation.sign

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.model.auth.AuthInfo
import com.hyeeyoung.wishboard.domain.usecase.auth.PostSignInEmailUseCase
import com.hyeeyoung.wishboard.domain.usecase.auth.PostSignInlUseCase
import com.hyeeyoung.wishboard.domain.usecase.auth.PostSignUpEmailUseCase
import com.hyeeyoung.wishboard.domain.usecase.auth.PostSignUpUseCase
import com.hyeeyoung.wishboard.domain.usecase.auth.PostVerificationMailUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.model.auth.SignUiModel
import com.hyeeyoung.wishboard.presentation.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor(
    private val postSignUpUseCase: PostSignUpUseCase,
    private val postSignInlUseCase: PostSignInlUseCase,
    private val postSignUpEmailUseCase: PostSignUpEmailUseCase,
    private val postVerificationMailUseCase: PostVerificationMailUseCase,
    private val postSignInEmailUseCase: PostSignInEmailUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(SignUiModel())
    val uiModel = _uiModel.asStateFlow()
    private var timerJob: Job? = null

    private fun initFCMToken(onSuccess: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result
                onSuccess(fcmToken)
                Timber.d(fcmToken)
            } else {
                Timber.e("Fetching FCM registration token failed", task.exception)
            }
        }
    }

    fun signUp(afterSuccess: () -> Unit) {
        if (_uiModel.value.signProcessStatus is WishBoardState.Loading) return
        _uiModel.update {
            it.copy(signProcessStatus = WishBoardState.Loading)
        }

        initFCMToken { fcmToken ->
            viewModelScope.launch {
                postSignUpUseCase(
                    AuthInfo(
                        email = uiModel.value.email,
                        password = uiModel.value.password,
                        fcmToken = fcmToken
                    )
                ).onSuccess {
                    _uiModel.update {
                        it.copy(signProcessStatus = WishBoardState.Success(Unit))
                    }
                    afterSuccess()
                }.onFailure { _, errorCode, _ ->
                    _uiModel.update {
                        it.copy(signProcessStatus = WishBoardState.Failure)
                    }
                }
            }
        }
    }

    fun signIn(afterSuccess: () -> Unit) {
        initFCMToken { fcmToken ->
            viewModelScope.launch {
                postSignInlUseCase(
                    AuthInfo(
                        email = uiModel.value.email,
                        password = uiModel.value.password,
                        fcmToken = fcmToken
                    )
                ).onSuccess {
                    afterSuccess()
                }.onFailure { _, errorCode, errorBody ->
                    when {
                        errorCode == 400 && errorBody?.contains("아이디 혹은 비밀번호를 다시 확인") == true -> updateSnackbarMessage("아이디 또는 비밀번호를 다시 확인해 주세요.")
                        else -> updateSnackbarMessage(SnackbarMessage.DEFAULT)
                    }
                }
            }
        }
    }

    fun requestVerificationMail(afterSuccess: () -> Unit) {
        viewModelScope.launch {
            postVerificationMailUseCase(
                email = uiModel.value.email,
            ).onSuccess {
                afterSuccess()
            }.onFailure { _, errorCode, _ ->
                when {
                    errorCode == 404 -> _uiModel.update {
                        it.copy(nonRegisteredEmail = uiModel.value.email)
                    }
                }
            }
        }
    }

    fun signInEmail(afterSuccess: () -> Unit) {
        initFCMToken { fcmToken ->
            viewModelScope.launch {
                postSignInEmailUseCase(
                    AuthInfo(
                        email = uiModel.value.email,
                        fcmToken = fcmToken
                    )
                ).onSuccess {
                    afterSuccess()
                }.onFailure { _, errorCode, errorBody ->
                    when {
                        errorCode == 404 && errorBody?.contains("유효하지 않은 인증번호") == true ->
                            _uiModel.update { it.copy(isCorrectAuthCode = false) }
                    }
                }
            }
        }
    }

    fun checkRegisteredUser(afterSuccess: () -> Unit) {
        viewModelScope.launch {
            postSignUpEmailUseCase(uiModel.value.email).onSuccess {
                afterSuccess()
            }.onFailure { _, errorCode, _ ->
                when (errorCode) {
                    409 -> _uiModel.update {
                        it.copy(registeredEmail = uiModel.value.email)
                    }
                }
            }
        }
    }

    fun onEmailChange(email: String) {
        val trimmedEmail = email.trim()
        _uiModel.update {
            it.copy(email = trimmedEmail)
        }

        val emailPattern = Patterns.EMAIL_ADDRESS
        _uiModel.update {
            it.copy(
                isValidEmail = if (email.isEmpty()) null else emailPattern.matcher(email).matches(),
            )
        }
    }

    fun onPasswordChange(password: String) {
        val trimmedPassword = password.trim()
        _uiModel.update {
            it.copy(password = trimmedPassword)
        }

        val passwordPattern = Pattern.compile(WishBoardFormat.PASSWORD_PATTERN)
        _uiModel.update {
            it.copy(isValidPassword = if (password.isBlank()) null else passwordPattern.matcher(password).matches())
        }
    }

    fun onAuthCodeChange(authCode: String) {
        val trimmedAuthCode = authCode.trim()
        _uiModel.update {
            it.copy(authCode = trimmedAuthCode, isCorrectAuthCode = false)
        }
    }

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var totalSeconds = 5 * 60
            while (totalSeconds >= 0) {
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                _uiModel.update {
                    it.copy(timer = String.format("%d:%02d", minutes, seconds))
                }
                delay(1000L)
                totalSeconds--
            }
        }
    }
}