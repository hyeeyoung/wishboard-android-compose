package com.hyeeyoung.wishboard.data.remote.repository

import com.hyeeyoung.wishboard.data.remote.model.folder.FolderNameDto
import com.hyeeyoung.wishboard.data.remote.service.FolderService
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val folderService: FolderService,
) : FolderRepository {
    override suspend fun fetchFolders(): Result<List<FolderItem>> = runCatching {
        folderService.fetchFolders().map { it.toDomain() }
    }

    override suspend fun fetchFolderDetail(folderId: Long): Result<List<WishItem>> = runCatching {
        folderService.fetchFolderDetail(folderId = folderId).map { it.toDomain() }
    }

    override suspend fun createFolder(folderName: String): Result<Unit> = runCatching {
        folderService.createFolder(folderName = FolderNameDto(folderName))
    }

    override suspend fun updateName(folderId: Long, folderName: String): Result<Unit> = runCatching {
        folderService.updateName(folderId = folderId, folderName = FolderNameDto(folderName))
    }

    override suspend fun deleteFolder(folderId: Long): Result<Unit> = runCatching {
        folderService.deleteFolder(folderId = folderId)
    }
}
