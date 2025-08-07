package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseNoData
import com.hyeeyoung.wishboard.data.remote.model.noti.NotiItemDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotiService {
    @GET("noti")
    suspend fun fetchPreviousNotiList(): List<NotiItemDto>

    @GET("noti/calendar")
    suspend fun fetchAllNotiList(): List<NotiItemDto>

    @PUT("noti/{item_id}/read-state")
    suspend fun updateNotiReadState(@Path("item_id") itemId: Long): BaseResponseNoData
}
