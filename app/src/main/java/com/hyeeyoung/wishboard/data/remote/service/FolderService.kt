package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseNoData
import com.hyeeyoung.wishboard.data.remote.model.base.PagedResponse
import com.hyeeyoung.wishboard.data.remote.model.folder.FolderItemDto
import com.hyeeyoung.wishboard.data.remote.model.folder.FolderNameDto
import com.hyeeyoung.wishboard.data.remote.model.folder.FolderSummaryDto
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FolderService {
    @GET("folder")
    suspend fun fetchFolders(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): BaseResponse<PagedResponse<FolderItemDto>>

    @GET("folder/list")
    suspend fun fetchFolderSummaries(): BaseResponse<List<FolderSummaryDto>>

    @GET("folder/item/{folderId}")
    suspend fun fetchFolderDetail(
        @Path("folderId") folderId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): BaseResponse<PagedResponse<WishItemDto>>

    @POST("folder")
    suspend fun createFolder(@Body folderName: FolderNameDto): BaseResponse<FolderSummaryDto>

    @PUT("folder/{folder_id}")
    suspend fun updateName(
        @Path("folder_id") folderId: Long,
        @Body folderName: FolderNameDto,
    ): BaseResponseNoData

    @DELETE("folder/{folderId}")
    suspend fun deleteFolder(@Path("folderId") folderId: Long): BaseResponseNoData
}
