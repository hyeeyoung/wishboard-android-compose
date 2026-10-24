package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class PutFolderThumbnailUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: Long, itemImageId: Long): Result<Unit> =
        repository.updateFolderThumbnail(folderId = folderId, itemImageId = itemImageId)
}
