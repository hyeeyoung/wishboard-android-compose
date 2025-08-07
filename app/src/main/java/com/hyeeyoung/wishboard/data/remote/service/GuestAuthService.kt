package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.interceptor.AuthInterceptor.Companion.DEVICE_INFO_HEADER_NAME
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.domain.model.auth.Token
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GuestAuthService {
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Header(DEVICE_INFO_HEADER_NAME) deviceInfoHeader: String,
        @Body token: Token,
    ): BaseResponse<Token>
}
