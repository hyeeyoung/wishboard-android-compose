package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.BulkDeleteScope
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import com.hyeeyoung.wishboard.domain.util.MAX_BULK_DELETE_ITEM_IDS
import javax.inject.Inject

class DeleteBulkWishItemsUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    // itemIds가 서버 허용치(500개)를 넘으면 여러 번의 요청으로 나눠 순차 호출한다.
    // excludeItemIds는 부분적으로 나눠 보낼 수 없으므로(각 조각이 독립적으로 "전체 삭제"를 수행하기 때문에)
    // 500개 이하로 들어온다는 전제로 그대로 한 번에 보낸다 — resolveBulkDeleteTarget이 이를 보장한다.
    suspend operator fun invoke(
        scope: BulkDeleteScope,
        folderId: Long? = null,
        itemStatus: WishItemOwnershipStatus? = null,
        itemIds: List<Long>? = null,
        excludeItemIds: List<Long>? = null,
    ): Result<Unit> {
        if (itemIds != null && itemIds.size > MAX_BULK_DELETE_ITEM_IDS) {
            for (chunk in itemIds.chunked(MAX_BULK_DELETE_ITEM_IDS)) {
                val result = repository.deleteBulkItems(
                    scope = scope,
                    folderId = folderId,
                    itemStatus = itemStatus,
                    itemIds = chunk,
                    excludeItemIds = null,
                )
                if (result.isFailure) return result
            }
            return Result.success(Unit)
        }

        return repository.deleteBulkItems(
            scope = scope,
            folderId = folderId,
            itemStatus = itemStatus,
            itemIds = itemIds,
            excludeItemIds = excludeItemIds,
        )
    }
}
