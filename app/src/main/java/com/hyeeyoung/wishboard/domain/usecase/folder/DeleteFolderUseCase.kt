package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class DeleteFolderUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: Long): Result<Unit> =
        repository.deleteFolder(folderId = folderId)
}
