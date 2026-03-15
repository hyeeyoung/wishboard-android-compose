package com.hyeeyoung.wishboard.presentation.my

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import com.hyeeyoung.wishboard.domain.model.user.UserProfile
import com.hyeeyoung.wishboard.domain.usecase.auth.PostLogoutUseCase
import com.hyeeyoung.wishboard.domain.usecase.user.DeleteUserAccountUseCase
import com.hyeeyoung.wishboard.domain.usecase.user.GetUserInfoUseCase
import com.hyeeyoung.wishboard.domain.usecase.user.PutPasswordUseCase
import com.hyeeyoung.wishboard.domain.usecase.user.PutUserProfileUseCase
import com.hyeeyoung.wishboard.domain.usecase.user.UpdatePushStateUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.my.model.MyUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import com.hyeeyoung.wishboard.presentation.util.WishBoardFormat
import com.hyeeyoung.wishboard.presentation.util.extension.compressImageToMaxSize
import com.hyeeyoung.wishboard.presentation.util.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val localStorage: WishBoardPreference,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val putUserProfileUseCase: PutUserProfileUseCase,
    private val updatePushStateUseCase: UpdatePushStateUseCase,
    private val putPasswordUseCase: PutPasswordUseCase,
    private val postLogoutUseCase: PostLogoutUseCase,
    private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
) : BaseViewModel() {
    private var _uiModel = MutableStateFlow(MyUiModel())
    val uiModel = _uiModel.asStateFlow()

    fun fetchUserInfo(isRefreshing: Boolean) {
        val needsFetch = isRefreshing ||
            uiModel.value.fetchProfileState !is WishBoardState.Success ||
            localStorage.userInfo.isPushAllowed == null
        if (!needsFetch) return
        if (uiModel.value.fetchProfileState == WishBoardState.Loading) return
        _uiModel.update {
            it.copy(fetchProfileState = WishBoardState.Loading, isRefreshing = isRefreshing)
        }

        viewModelScope.launch {
            getUserInfoUseCase().onSuccess { userInfo ->
                _uiModel.update {
                    it.copy(
                        fetchProfileState = WishBoardState.Success(Unit),
                        userInfo = userInfo,
                        isRefreshing = false,
                    )
                }
            }.onFailure { exception, _, _ ->
                _uiModel.update {
                    it.copy(fetchProfileState = WishBoardState.Failure, isRefreshing = false)
                }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun updatePushState(isPushAllowed: Boolean) {
        viewModelScope.launch {
            updatePushStateUseCase(isPushAllowed).onSuccess {
                _uiModel.update {
                    it.copy(userInfo = it.userInfo.copy(isPushAllowed = isPushAllowed))
                }
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun updateUserProfile(context: Context, afterSuccess: () -> Unit) {
        if (uiModel.value.updateProfileState is WishBoardState.Loading) return
        _uiModel.update {
            it.copy(updateProfileState = WishBoardState.Loading)
        }
        val trimmedName = uiModel.value.nicknameInput.text.trim()
        val file = uiModel.value.imageUriInput?.let { uri ->
            context.compressImageToMaxSize(uri)
        }
        val requestBody = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())

        viewModelScope.launch {
            putUserProfileUseCase(
                userProfile = UserProfile(
                    nickName = trimmedName,
                    profileImage = safeLet(file, requestBody) { file, requestBody ->
                        MultipartBody.Part.createFormData("profileImage", file.name, requestBody)
                    },
                ),
            ).onSuccess {
                _uiModel.update {
                    it.copy(updateProfileState = WishBoardState.Success(Unit))
                }
                afterSuccess()
                updateSnackbarMessage("프로필이 수정되었어요!👩‍🎤")
            }.onFailure { exception, errorCode, _ ->
                _uiModel.update {
                    it.copy(updateProfileState = WishBoardState.Failure)
                }
                when (errorCode) {
                    409 -> _uiModel.update { it.copy(existingNickname = trimmedName) }
                    else -> updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
                }
            }
        }
    }

    fun updatePassword(afterSuccess: () -> Unit) {
        if (uiModel.value.updatePasswordState is WishBoardState.Loading) return
        _uiModel.update {
            it.copy(updatePasswordState = WishBoardState.Loading)
        }
        viewModelScope.launch {
            putPasswordUseCase(uiModel.value.rePasswordInput).onSuccess {
                _uiModel.update {
                    it.copy(updatePasswordState = WishBoardState.Loading)
                }
                updateSnackbarMessage("비밀번호가 변경되었어요!👩‍🎤")
                afterSuccess()
            }.onFailure { exception, _, _ ->
                _uiModel.update {
                    it.copy(updatePasswordState = WishBoardState.Failure)
                }
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun logout(afterSuccess: () -> Unit) {
        viewModelScope.launch {
            postLogoutUseCase().onSuccess {
                afterSuccess()
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun deleteAccount(afterSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteUserAccountUseCase().onSuccess {
                afterSuccess()
                updateSnackbarMessage("탈퇴 완료되었어요. 이용해주셔서 감사합니다!☺️")
            }.onFailure { exception, _, _ ->
                updateSnackbarMessage(message = SnackbarMessage.DEFAULT, exception = exception)
            }
        }
    }

    fun onNicknameChange(nickname: TextFieldValue) {
        _uiModel.update {
            it.copy(nicknameInput = nickname)
        }
    }

    fun setProfileImageUri(uri: Uri?) {
        Timber.e("uri : $uri")
        _uiModel.update {
            it.copy(imageUriInput = uri)
        }
    }

    fun onPasswordChange(password: String, isRePassword: Boolean) {
        val trimmedPassword = password.trim()

        _uiModel.update {
            if (isRePassword) {
                it.copy(rePasswordInput = trimmedPassword)
            } else {
                val passwordPattern = Pattern.compile(WishBoardFormat.PASSWORD_PATTERN)
                val isValid = if (trimmedPassword.isBlank()) {
                    null
                } else {
                    passwordPattern.matcher(password).matches()
                }
                it.copy(passwordInput = trimmedPassword, isValidPassword = isValid)
            }
        }
    }

    fun setTokenForProfileImageUri() {
        _uiModel.update {
            it.copy(accessToken = localStorage.accessToken)
        }
    }

    fun setOriginalUserInfo(userInfo: UserInfo) {
        _uiModel.update {
            it.copy(
                userInfo = userInfo,
                nicknameInput = TextFieldValue(
                    text = userInfo.nickname,
                    selection = TextRange(userInfo.nickname.length),
                ),
            )
        }
    }
}
