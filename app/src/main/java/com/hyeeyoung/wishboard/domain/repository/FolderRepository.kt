package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem

interface FolderRepository {
    suspend fun fetchFolders(): Result<List<FolderItem>>

    suspend fun fetchFolderDetail(folderId: Long): Result<List<WishItem>>

    suspend fun createFolder(folderName: String): Result<Unit>

    suspend fun updateName(folderId: Long, folderName: String): Result<Unit>

    suspend fun deleteFolder(folderId: Long): Result<Unit>
}
