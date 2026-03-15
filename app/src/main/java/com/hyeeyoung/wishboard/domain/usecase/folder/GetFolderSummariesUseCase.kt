package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class GetFolderSummariesUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(): Result<List<FolderItem>> =
        repository.fetchFolderSummaries()
}
