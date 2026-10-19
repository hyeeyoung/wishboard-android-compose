package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class DeleteFolderThumbnailUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: Long): Result<Unit> =
        repository.deleteFolderThumbnail(folderId = folderId)
}
