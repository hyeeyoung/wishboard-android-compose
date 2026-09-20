package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class PutFolderOrderUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: List<Long>): Result<Unit> =
        repository.reorderFolders(folderId)
}
