package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.system.AppVersionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SystemService {
    @GET("version/check")
    suspend fun fetchAppVersion(
        @Query("osType") osType: String = "AOS",
    ): BaseResponse<AppVersionDto>
}
