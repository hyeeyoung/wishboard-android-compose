package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseWithoutData
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemDetailDto
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemIdDto
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ItemService {
    @GET("item")
    suspend fun fetchWishList(): List<WishItemDto>

    @GET("item/{item_id}")
    suspend fun fetchWishItemDetail(
        @Path("item_id") itemId: Long,
    ): List<WishItemDetailDto>

    @Multipart
    @POST("item")
    suspend fun uploadWishItem(
        @Query("type") type: String,
        @Part("folder_id") folderId: RequestBody?,
        @Part("item_name") itemName: RequestBody,
        @Part("item_price") itemPrice: RequestBody?,
        @Part("item_memo") itemMemo: RequestBody?,
        @Part("item_url") itemUrl: RequestBody?,
        @Part("item_notification_type") itemNotificationType: RequestBody?,
        @Part("item_notification_date") itemNotificationDate: RequestBody?,
        @Part itemImg: MultipartBody.Part?,
    ): BaseResponse<WishItemIdDto>

    @Multipart
    @PUT("item/{item_id}")
    suspend fun updateWishItem(
        @Path("item_id") itemId: Long,
        @Part("folder_id") folderId: RequestBody?,
        @Part("item_name") itemName: RequestBody,
        @Part("item_price") itemPrice: RequestBody?,
        @Part("item_memo") itemMemo: RequestBody?,
        @Part("item_url") itemUrl: RequestBody?,
        @Part("item_notification_type") itemNotificationType: RequestBody?,
        @Part("item_notification_date") itemNotificationDate: RequestBody?,
        @Part itemImg: MultipartBody.Part?,
    ): BaseResponseWithoutData

    @PUT("item/{item_id}/folder/{folder_id}")
    suspend fun updateFolderOfItem(
        @Path("item_id") itemId: Long,
        @Path("folder_id") folderId: Long,
    ): BaseResponseWithoutData

    @DELETE("item/{item_id}")
    suspend fun deleteWishItem(@Path("item_id") itemId: Long): BaseResponseWithoutData

    @GET("item/parse")
    suspend fun getParsedItemInfo(
        @Query("site") site: String,
    ): BaseResponse<ParsedWishItem>
}
