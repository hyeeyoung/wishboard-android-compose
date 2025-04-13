package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.auth.AuthResponseDto
import com.hyeeyoung.wishboard.data.remote.model.auth.RefreshTokenDto
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GuestAuthService {
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body refreshToken: RefreshTokenDto,
    ): BaseResponse<AuthResponseDto>
}
