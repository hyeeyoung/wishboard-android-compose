package com.hyeeyoung.wishboard.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hyeeyoung.wishboard.data.remote.model.common.PageSize
import com.hyeeyoung.wishboard.data.remote.model.folder.FolderNameDto
import com.hyeeyoung.wishboard.data.remote.model.folder.ReorderFolderListDto
import com.hyeeyoung.wishboard.data.remote.paging.GeneralPagingSource
import com.hyeeyoung.wishboard.data.remote.service.FolderService
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.folder.FolderOrderOption
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val folderService: FolderService,
) : FolderRepository {
    override fun fetchFolders(): Flow<PagingData<FolderItem>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = PageSize.DEFAULT_SIZE,
                pageSize = PageSize.DEFAULT_SIZE,
                enablePlaceholders = true,
                prefetchDistance = PageSize.DEFAULT_PREFETCH_SIZE,
            ),
            pagingSourceFactory = {
                GeneralPagingSource(
                    loadPage = { page, size ->
                        folderService.fetchFolders(page = page, size = size)
                    },
                )
            },
        ).flow.map {
            it.map { it.toDomain() }
        }

    override suspend fun fetchFolderSummaries(orderOption: FolderOrderOption): Result<List<FolderItem>> = runCatching {
        folderService.fetchFolderSummaries(orderOption.name).data.map { it.toDomain() }
    }

    override fun fetchFolderDetail(folderId: Long): Flow<PagingData<WishItem>> = Pager(
        config = PagingConfig(
            initialLoadSize = PageSize.DEFAULT_SIZE,
            pageSize = PageSize.DEFAULT_SIZE,
            enablePlaceholders = true,
            prefetchDistance = PageSize.DEFAULT_PREFETCH_SIZE,
        ),
        pagingSourceFactory = {
            GeneralPagingSource(
                loadPage = { page, size ->
                    folderService.fetchFolderDetail(folderId = folderId, page = page, size = size)
                },
            )
        },
    ).flow.map {
        it.map { it.toDomain() }
    }

    override suspend fun createFolder(folderName: String): Result<FolderItem> = runCatching {
        folderService.createFolder(folderName = FolderNameDto(folderName)).data.toDomain()
    }

    override suspend fun updateName(folderId: Long, folderName: String): Result<Unit> = runCatching {
        folderService.updateName(folderId = folderId, folderName = FolderNameDto(folderName))
    }

    override suspend fun deleteFolder(folderId: Long): Result<Unit> = runCatching {
        folderService.deleteFolder(folderId = folderId)
    }

    override suspend fun reorderFolders(folderIds: List<Long>): Result<Unit> = runCatching {
        folderService.reorderFolders(folderIds = ReorderFolderListDto(folderIds))
    }
}
