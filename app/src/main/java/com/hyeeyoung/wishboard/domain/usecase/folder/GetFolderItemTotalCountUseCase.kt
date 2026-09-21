package com.hyeeyoung.wishboard.domain.usecase.folder

import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import javax.inject.Inject

class GetFolderItemTotalCountUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    operator fun invoke() = repository.folderDetailTotalElements
}
