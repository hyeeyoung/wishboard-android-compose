package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class PutWishItemOwnershipUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(itemId: Long, isOwnedItem: Boolean): Result<Boolean> =
        repository.updateItemOwnership(itemId = itemId, isOwnedItem = isOwnedItem)
}
