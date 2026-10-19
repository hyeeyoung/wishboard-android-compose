package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class DeleteFolderOfWishItemUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(itemId: Long, folderId: Long): Result<Unit> =
        repository.deleteFolderOfWishItem(itemId = itemId, folderId = folderId)
}
