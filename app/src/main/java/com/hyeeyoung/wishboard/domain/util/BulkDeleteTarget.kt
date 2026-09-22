package com.hyeeyoung.wishboard.domain.util

import com.hyeeyoung.wishboard.domain.model.wish.BulkDeleteScope

// 서버가 itemIds 한 번 요청당 허용하는 최대 개수. 초과분은 DeleteBulkWishItemsUseCase가 청크로 나눠 보낸다.
const val MAX_BULK_DELETE_ITEM_IDS = 500

data class BulkDeleteTarget(
    val scope: BulkDeleteScope,
    val itemIds: List<Long>? = null,
    val excludeItemIds: List<Long>? = null,
)

// scope=ALL + excludeItemIds 조합은 조회 필터(itemStatus)와 함께 보낼 수 없다는 서버 제약이 있어
// (400: "선택 삭제 요청에는 조회 필터와 제외 목록을 함께 보낼 수 없습니다") 사용하지 않는다.
// 전체 선택(제외 없음) 자체는 필터와 함께 보내도 문제없으므로 그대로 scope=ALL을 쓴다.
fun resolveBulkDeleteTarget(
    isAllSelected: Boolean,
    selectedItemIds: Set<Long>,
    excludedItemIds: Set<Long> = emptySet(),
    allLoadedItemIds: List<Long>,
): BulkDeleteTarget {
    if (isAllSelected) {
        if (excludedItemIds.isEmpty()) return BulkDeleteTarget(scope = BulkDeleteScope.ALL)

        // 로드된 아이템에서 제외 대상을 뺀 itemIds 방식(청크 삭제)으로 대체한다.
        // 전체 목록이 다 로드되지 않았다면 아직 보지 못한 아이템은 이번엔 삭제되지 않는다.
        return BulkDeleteTarget(
            scope = BulkDeleteScope.SELECTED,
            itemIds = allLoadedItemIds.filterNot { excludedItemIds.contains(it) },
        )
    }

    return BulkDeleteTarget(scope = BulkDeleteScope.SELECTED, itemIds = selectedItemIds.toList())
}
