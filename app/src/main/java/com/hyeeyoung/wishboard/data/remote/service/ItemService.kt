package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseWithoutData
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemDetailDto
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ItemService {
    @GET("item")
    suspend fun fetchWishList(): List<WishItemDto>

    @GET("item/{item_id}")
    suspend fun fetchWishItemDetail(
        @Path("item_id") itemId: Long
    ): List<WishItemDetailDto>

//    @Multipart
//    @POST("/item")
//    suspend fun uploadWishItem(
//        @Part("folder_id") folderId: RequestBody?,
//        @Part("item_name") itemName: RequestBody,
//        @Part("item_price") itemPrice: RequestBody?,
//        @Part("item_memo") itemMemo: RequestBody?,
//        @Part("item_url") itemUrl: RequestBody?,
//        @Part("item_notification_type") itemNotificationType: RequestBody?,
//        @Part("item_notification_date") itemNotificationDate: RequestBody?,
//        @Part itemImg: MultipartBody.Part?,
//    ): Response<BaseResponseResult<BaseResponseData?>>
//
//    @Multipart
//    @PUT("item/{item_id}")
//    suspend fun updateToWishItem(
//        @Path("item_id") itemId: Long,
//        @Part("folder_id") folderId: RequestBody?,
//        @Part("item_name") itemName: RequestBody,
//        @Part("item_price") itemPrice: RequestBody?,
//        @Part("item_memo") itemMemo: RequestBody?,
//        @Part("item_url") itemUrl: RequestBody?,
//        @Part("item_notification_type") itemNotificationType: RequestBody?,
//        @Part("item_notification_date") itemNotificationDate: RequestBody?,
//        @Part itemImg: MultipartBody.Part?,
//    ): Response<BaseResponseResult<BaseResponseData?>>
//
    @PUT("item/{item_id}/folder/{folder_id}")
    suspend fun updateFolderOfItem(
        @Path("item_id") itemId: Long,
        @Path("folder_id") folderId: Long
    ): BaseResponseWithoutData
//
//    @DELETE("item/{item_id}")
//    suspend fun deleteWishItem(@Path("item_id") itemId: Long): Response<BaseResponseResult<BaseResponseData?>>
//
//    @GET("item/parse?site=")
//    suspend fun getItemParsingInfo(
//        @Query("site") site: String
//    ): Response<BaseResponseResult<ItemInfo>?>
}
