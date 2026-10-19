package com.hyeeyoung.wishboard.domain.repository

import androidx.paging.PagingData
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.folder.FolderOrderOption
import com.hyeeyoung.wishboard.domain.model.folder.FolderThumbnailImage
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FolderRepository {
    val folderDetailTotalElements: StateFlow<Int?>

    fun fetchFolders(): Flow<PagingData<FolderItem>>

    suspend fun fetchFolderSummaries(orderOption: FolderOrderOption): Result<List<FolderItem>>

    fun fetchFolderDetail(folderId: Long, itemStatus: WishItemOwnershipStatus? = null): Flow<PagingData<WishItem>>

    suspend fun createFolder(folderName: String): Result<FolderItem>

    suspend fun updateName(folderId: Long, folderName: String): Result<Unit>

    suspend fun deleteFolder(folderId: Long): Result<Unit>

    suspend fun reorderFolders(folderIds: List<Long>): Result<Unit>

    /** 대표 사진 후보를 모든 페이지에서 불러온다. 현재 적용된 대표 사진은 [FolderThumbnailImage.isSelected]로 구분한다. */
    suspend fun fetchFolderThumbnailImages(folderId: Long): Result<List<FolderThumbnailImage>>

    suspend fun updateFolderThumbnail(folderId: Long, itemImageId: Long): Result<Unit>

    /** 지정한 대표 사진을 해제하고 기본 대표 사진으로 되돌린다. */
    suspend fun deleteFolderThumbnail(folderId: Long): Result<Unit>
}
