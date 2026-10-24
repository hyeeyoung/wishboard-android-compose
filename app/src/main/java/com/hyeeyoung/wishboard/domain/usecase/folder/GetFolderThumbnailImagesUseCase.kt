package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.model.folder.FolderThumbnailImage
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class GetFolderThumbnailImagesUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: Long): Result<List<FolderThumbnailImage>> =
        repository.fetchFolderThumbnailImages(folderId = folderId)
}
