package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseWithoutData
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

interface FolderService {
    @GET("folder")
    suspend fun fetchFolders(): BaseResponse<List<FolderItemDto>>

    @GET("folder/list")
    suspend fun fetchFolderSummaries(): BaseResponse<List<FolderSummaryDto>>

    @GET("folder/item/{folder_id}")
    suspend fun fetchFolderDetail(@Path("folder_id") folderId: Long): List<WishItemDto>

    @POST("folder")
    suspend fun createFolder(@Body folderName: FolderNameDto): BaseResponse<FolderSummaryDto>

    @PUT("folder/{folder_id}")
    suspend fun updateName(
        @Path("folder_id") folderId: Long,
        @Body folderName: FolderNameDto,
    ): BaseResponseWithoutData

    @DELETE("folder/{folder_id}")
    suspend fun deleteFolder(@Path("folder_id") folderId: Long): BaseResponseWithoutData
}
