package com.hyeeyoung.wishboard.data.remote.repository

import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.remote.model.auth.AuthRequestDto
import com.hyeeyoung.wishboard.data.remote.model.auth.EmailAuthRequestDto
import com.hyeeyoung.wishboard.data.remote.model.auth.EmailCheckRequestDto
import com.hyeeyoung.wishboard.data.remote.model.auth.VerificationMailRequestDto
import com.hyeeyoung.wishboard.data.remote.service.AuthService
import com.hyeeyoung.wishboard.domain.model.auth.AuthInfo
import com.hyeeyoung.wishboard.domain.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val localStorage: WishBoardPreference,
) : AuthRepository {
    override suspend fun signUp(authInfo: AuthInfo): Result<Unit> = runCatching {
        authService.signUp(AuthRequestDto.fromDomain(authInfo)).data
    }.onSuccess {
        Timber.d("회원가입 성공")
        localStorage.setUserInfo(
            authInfo.email,
            it.tempNickname,
            it.token.accessToken,
            it.token.refreshToken
        )
        localStorage.shouldShowOnboardingModal = true
    }.map { }

    override suspend fun signIn(authInfo: AuthInfo): Result<Unit> = runCatching {
        authService.signIn(AuthRequestDto.fromDomain(authInfo)).data
    }.onSuccess {
        Timber.d("회원가입 성공")
        localStorage.setUserInfo(
            authInfo.email,
            it.tempNickname,
            it.token.accessToken,
            it.token.refreshToken
        )
    }.map { }

    override suspend fun requestVerificationMail(email: String): Result<String> = runCatching {
        authService.requestVerificationMail(VerificationMailRequestDto(email)).data.verificationCode
    }

    override suspend fun signInEmail(authInfo: AuthInfo): Result<Unit> = runCatching {
        authService.signInEmail(EmailAuthRequestDto.fromDomain(authInfo)).data
    }.onSuccess {
        Timber.d("이메일 로그인 성공")
        localStorage.setUserInfo(
            authInfo.email,
            it.tempNickname,
            it.token.accessToken,
            it.token.refreshToken
        )
    }.map { }

    override suspend fun checkRegisteredUser(email: String): Result<Unit> = runCatching {
        authService.checkRegisteredUser(EmailCheckRequestDto(email))
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        authService.logout()
    }.onSuccess {
        localStorage.clear()
    }.map {  }
}
