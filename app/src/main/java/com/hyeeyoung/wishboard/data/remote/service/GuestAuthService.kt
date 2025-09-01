package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.domain.model.auth.Token
import retrofit2.http.Body
import retrofit2.http.POST

interface GuestAuthService {
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body token: Token,
    ): BaseResponse<Token>
}
