package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class PutFolderNameUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: Long, folderName: String): Result<Unit> =
        repository.updateName(folderId = folderId, folderName = folderName)
}
