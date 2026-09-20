package com.hyeeyoung.wishboard.domain.util

import com.hyeeyoung.wishboard.domain.model.wish.BulkDeleteScope

// 서버가 itemIds/excludeItemIds 한 번 요청당 허용하는 최대 개수.
const val MAX_BULK_DELETE_ITEM_IDS = 500

data class BulkDeleteTarget(
    val scope: BulkDeleteScope,
    val itemIds: List<Long>? = null,
    val excludeItemIds: List<Long>? = null,
)

// 선택된 아이템이 전체의 절반을 넘으면 itemIds 대신 scope=ALL + excludeItemIds로 요청 본문을 줄인다.
// 다만 전체 목록이 다 로드되지 않은 상태에서는 아직 보지 못한 아이템까지 삭제될 수 있으므로,
// allLoadedItemIds가 totalItemCount만큼 다 로드됐을 때만 이 최적화를 적용한다.
fun resolveBulkDeleteTarget(
    isAllSelected: Boolean,
    selectedItemIds: Set<Long>,
    allLoadedItemIds: List<Long>,
    totalItemCount: Int?,
): BulkDeleteTarget {
    if (isAllSelected) return BulkDeleteTarget(scope = BulkDeleteScope.ALL)

    val total = totalItemCount ?: allLoadedItemIds.size
    val isFullyLoaded = allLoadedItemIds.size >= total
    val isMajoritySelected = total > 0 && selectedItemIds.size * 2 > total

    if (isFullyLoaded && isMajoritySelected) {
        val excludeItemIds = allLoadedItemIds.filterNot { selectedItemIds.contains(it) }
        // excludeItemIds 자체가 500개를 넘으면 여러 요청으로 나눌 수 없다 — 각 조각이 독립적으로
        // "전체 삭제, 이 조각만 제외"로 동작해서 서로 다른 조각이 지키려던 아이템까지 지워버리기 때문이다.
        // 이 경우엔 안전하게 itemIds 방식으로 되돌려서 500개 단위 청크 삭제가 가능하게 한다.
        if (excludeItemIds.size <= MAX_BULK_DELETE_ITEM_IDS) {
            return BulkDeleteTarget(scope = BulkDeleteScope.ALL, excludeItemIds = excludeItemIds)
        }
    }

    return BulkDeleteTarget(scope = BulkDeleteScope.SELECTED, itemIds = selectedItemIds.toList())
}
