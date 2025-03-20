package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem

interface FolderRepository {
    suspend fun fetchFolders(): Result<List<FolderItem>>

    suspend fun fetchFolderDetail(folderId: Long): Result<List<WishItem>>
}
