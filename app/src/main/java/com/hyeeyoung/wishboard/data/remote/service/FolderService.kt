package com.hyeeyoung.wishboard.data.remote.service

import com.hyeeyoung.wishboard.data.remote.model.folder.FolderItemDto
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemDto
import retrofit2.http.GET
import retrofit2.http.Path

interface FolderService {
    @GET("folder")
    suspend fun fetchFolders(): List<FolderItemDto>

    @GET("folder/item/{folder_id}")
    suspend fun fetchFolderDetail(@Path("folder_id") folderId: Long): List<WishItemDto>

//    @POST("folder")
//    suspend fun createNewFolder(@Body folderItem: FolderItem): Response<BaseResponseResult<BaseResponseData>>
//
//    @FormUrlEncoded
//    @PUT("folder/{folder_id}")
//    suspend fun updateFolderName(
//        @Path("folder_id") folderId: Long,
//        @Field("folder_name") folderName: String
//    ): Response<BaseResponseResult<BaseResponseData?>>
//
//    @DELETE("folder/{folder_id}")
//    suspend fun deleteFolder(@Path("folder_id") folderId: Long): Response<BaseResponseResult<BaseResponseData?>>
}
