package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseNoData
import com.hyeeyoung.wishboard.data.remote.model.base.PagedResponse
import com.hyeeyoung.wishboard.data.remote.model.wish.DeleteBulkItemsRequestDto
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemDetailDto
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemIdDto
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemOwnershipRequestDto
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ItemService {
    @GET("item")
    suspend fun fetchWishList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("itemStatus") itemStatus: String? = null,
    ): BaseResponse<PagedResponse<WishItemDto>>

    @GET("item/{itemId}")
    suspend fun fetchWishItemDetail(
        @Path("itemId") itemId: Long,
    ): BaseResponse<WishItemDetailDto>

    @Multipart
    @POST("item")
    suspend fun uploadWishItem(
        @Query("type") type: String,
        @Part("request") item: RequestBody,
        @Part itemImg: List<MultipartBody.Part>?,
    ): BaseResponse<WishItemIdDto>

    @Multipart
    @PUT("item/{item_id}")
    suspend fun updateWishItem(
        @Path("item_id") itemId: Long,
        @Part("request") item: RequestBody,
        @Part itemImg: List<MultipartBody.Part>?,
    ): BaseResponseNoData

    @PUT("item/{itemId}/folder/{folderId}")
    suspend fun updateFolderOfItem(
        @Path("itemId") itemId: Long,
        @Path("folderId") folderId: Long,
    ): BaseResponseNoData

    @DELETE("item/{itemId}")
    suspend fun deleteWishItem(@Path("itemId") itemId: Long): BaseResponseNoData

    // DELETE는 Retrofit에서 기본적으로 @Body를 허용하지 않아, body를 함께 보내려면 @HTTP로 명시해야 한다.
    @HTTP(method = "DELETE", path = "item/bulk", hasBody = true)
    suspend fun deleteBulkItems(
        @Query("scope") scope: String,
        @Query("folderId") folderId: Long? = null,
        @Query("itemStatus") itemStatus: String? = null,
        @Body request: DeleteBulkItemsRequestDto,
    ): BaseResponseNoData

    @GET("item/parse")
    suspend fun getParsedItemInfo(
        @Query("site") site: String,
    ): BaseResponse<ParsedWishItem?>

    @PUT("item/{itemId}/status")
    suspend fun updateItemOwnership(
        @Path("itemId") itemId: Long,
        @Body ownership: WishItemOwnershipRequestDto,
    ): BaseResponse<WishItemDetailDto>
}
