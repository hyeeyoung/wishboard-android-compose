package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseNoData
import com.hyeeyoung.wishboard.data.remote.model.user.PasswordDto
import com.hyeeyoung.wishboard.data.remote.model.user.UserInfoDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {
    @GET("user")
    suspend fun fetchUserInfo(): BaseResponse<UserInfoDto>

    @Multipart
    @PUT("user")
    suspend fun updateUserInfo(
        @Part("request") nickname: RequestBody,
        @Part profileImg: MultipartBody.Part?,
    ): BaseResponseNoData

    @DELETE("user")
    suspend fun deleteUserAccount(): BaseResponseNoData

    @PUT("user/push-state/{push}")
    suspend fun updatePushState(@Path("pushState") push: Boolean): BaseResponseNoData

    @PUT("user/re-passwd")
    suspend fun changePassword(@Body password: PasswordDto): BaseResponseNoData
}
