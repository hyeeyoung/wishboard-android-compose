package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.BulkDeleteScope
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class DeleteBulkWishItemsUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(
        scope: BulkDeleteScope,
        folderId: Long? = null,
        itemStatus: WishItemOwnershipStatus? = null,
        itemIds: List<Long>? = null,
        excludeItemIds: List<Long>? = null,
    ): Result<Unit> = repository.deleteBulkItems(
        scope = scope,
        folderId = folderId,
        itemStatus = itemStatus,
        itemIds = itemIds,
        excludeItemIds = excludeItemIds,
    )
}
