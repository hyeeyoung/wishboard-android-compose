package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class GetFolderDetailUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: Long): Result<List<WishItem>> =
        repository.fetchFolderDetail(folderId = folderId)
}
