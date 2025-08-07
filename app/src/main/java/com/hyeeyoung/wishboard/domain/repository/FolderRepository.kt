package com.hyeeyoung.wishboard.domain.repository

import androidx.paging.PagingData
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun fetchFolders(): Flow<PagingData<FolderItem>>

    suspend fun fetchFolderSummaries(): Result<List<FolderItem>>

    suspend fun fetchFolderDetail(folderId: Long): Result<List<WishItem>>

    suspend fun createFolder(folderName: String): Result<FolderItem>

    suspend fun updateName(folderId: Long, folderName: String): Result<Unit>

    suspend fun deleteFolder(folderId: Long): Result<Unit>
}
