package com.hyeeyoung.wishboard.domain.usecase.folder

import androidx.paging.PagingData
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFolderDetailUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    operator fun invoke(folderId: Long): Flow<PagingData<WishItem>> =
        repository.fetchFolderDetail(folderId = folderId)
}
