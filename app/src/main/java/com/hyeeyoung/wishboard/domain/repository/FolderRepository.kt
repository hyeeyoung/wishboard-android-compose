package com.hyeeyoung.wishboard.domain.repository

import androidx.paging.PagingData
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.folder.FolderOrderOption
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun fetchFolders(): Flow<PagingData<FolderItem>>

    suspend fun fetchFolderSummaries(orderOption: FolderOrderOption): Result<List<FolderItem>>

    fun fetchFolderDetail(folderId: Long): Flow<PagingData<WishItem>>

    suspend fun createFolder(folderName: String): Result<FolderItem>

    suspend fun updateName(folderId: Long, folderName: String): Result<Unit>

    suspend fun deleteFolder(folderId: Long): Result<Unit>

    suspend fun reorderFolders(folderIds: List<Long>): Result<Unit>
}
