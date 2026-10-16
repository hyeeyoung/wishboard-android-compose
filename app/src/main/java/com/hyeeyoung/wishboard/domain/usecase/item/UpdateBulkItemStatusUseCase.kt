package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.BulkDeleteScope
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class UpdateBulkItemStatusUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(
        scope: BulkDeleteScope,
        itemStatus: WishItemOwnershipStatus? = null,
        targetStatus: WishItemOwnershipStatus,
        itemIds: List<Long>? = null,
    ): Result<Unit> = repository.updateBulkItemStatus(
        scope = scope,
        itemStatus = itemStatus,
        targetStatus = targetStatus,
        itemIds = itemIds,
    )
}
