package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.BulkDeleteScope
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import com.hyeeyoung.wishboard.domain.util.MAX_BULK_DELETE_ITEM_IDS
import javax.inject.Inject

class DeleteBulkWishItemsFromFolderUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    // itemIds가 서버 허용치(500개)를 넘으면 여러 번의 요청으로 나눠 순차 호출한다.
    // excludeItemIds는 나눠 보낼 수 없으므로 500개 이하로 들어온다는 전제로 그대로 한 번에 보낸다.
    suspend operator fun invoke(
        folderId: Long,
        scope: BulkDeleteScope,
        itemStatus: WishItemOwnershipStatus? = null,
        itemIds: List<Long>? = null,
        excludeItemIds: List<Long>? = null,
    ): Result<Unit> {
        // scope=SELECTED에서는 itemStatus를 함께 보내면 서버가 400을 반환하므로 보내지 않는다.
        val scopedItemStatus = itemStatus.takeIf { scope == BulkDeleteScope.ALL }

        if (itemIds != null && itemIds.size > MAX_BULK_DELETE_ITEM_IDS) {
            for (chunk in itemIds.chunked(MAX_BULK_DELETE_ITEM_IDS)) {
                val result = repository.deleteBulkItemsFromFolder(
                    folderId = folderId,
                    scope = scope,
                    itemStatus = scopedItemStatus,
                    itemIds = chunk,
                    excludeItemIds = null,
                )
                if (result.isFailure) return result
            }
            return Result.success(Unit)
        }

        return repository.deleteBulkItemsFromFolder(
            folderId = folderId,
            scope = scope,
            itemStatus = scopedItemStatus,
            itemIds = itemIds,
            excludeItemIds = excludeItemIds,
        )
    }
}
