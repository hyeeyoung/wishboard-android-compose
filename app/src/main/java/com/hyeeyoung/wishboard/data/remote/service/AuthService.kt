package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.interceptor.AuthInterceptor.Companion.DEVICE_INFO_HEADER_NAME
import com.hyeeyoung.wishboard.data.remote.model.auth.AuthRequestDto
import com.hyeeyoung.wishboard.data.remote.model.auth.AuthResponseDto
import com.hyeeyoung.wishboard.data.remote.model.auth.EmailAuthRequestDto
import com.hyeeyoung.wishboard.data.remote.model.auth.EmailCheckRequestDto
import com.hyeeyoung.wishboard.data.remote.model.auth.VerificationMailDto
import com.hyeeyoung.wishboard.data.remote.model.auth.VerificationMailRequestDto
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseNoData
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signUp(
        @Header(DEVICE_INFO_HEADER_NAME) deviceInfoHeader: String,
        @Body authInfo: AuthRequestDto,
    ): BaseResponse<AuthResponseDto>

    @POST("auth/signin")
    suspend fun signIn(
        @Header(DEVICE_INFO_HEADER_NAME) deviceInfoHeader: String,
        @Body authInfo: AuthRequestDto,
    ): BaseResponse<AuthResponseDto>

    @POST("auth/re-signin")
    suspend fun signInEmail(
        @Header(DEVICE_INFO_HEADER_NAME) deviceInfoHeader: String,
        @Body authInfo: EmailAuthRequestDto,
    ): BaseResponse<AuthResponseDto>

    @POST("auth/check-email")
    suspend fun checkRegisteredUser(
        @Body email: EmailCheckRequestDto,
    ): BaseResponseNoData

    @POST("auth/password-mail")
    suspend fun requestVerificationMail(
        @Body email: VerificationMailRequestDto,
    ): BaseResponse<VerificationMailDto>

    @POST("auth/logout")
    suspend fun logout(
        @Header(DEVICE_INFO_HEADER_NAME) deviceInfoHeader: String,
    ): BaseResponseNoData
}
