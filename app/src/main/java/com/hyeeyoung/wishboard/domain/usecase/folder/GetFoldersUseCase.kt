package com.hyeeyoung.wishboard.domain.usecase.folder

import androidx.paging.PagingData
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoldersUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    operator fun invoke(): Flow<PagingData<FolderItem>> =
        repository.fetchFolders()
}
