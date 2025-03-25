package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseWithoutData
import com.hyeeyoung.wishboard.data.remote.model.user.PasswordDto
import com.hyeeyoung.wishboard.data.remote.model.user.UserInfoDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {
    @GET("user")
    suspend fun fetchUserInfo(): List<UserInfoDto>

    @Multipart
    @PUT("user")
    suspend fun updateUserInfo(
        @Part("nickname") nickname: RequestBody?,
        @Part profileImg: MultipartBody.Part?
    ): BaseResponseWithoutData

    @PUT("user/active")
    suspend fun deleteUserAccount(): BaseResponseWithoutData

    @PUT("user/push-state/{push}")
    suspend fun updatePushState(@Path("push") push: Boolean): BaseResponseWithoutData

    @PUT("user/re-passwd")
    suspend fun changePassword(@Body password: PasswordDto): BaseResponseWithoutData
}
