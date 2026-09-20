package com.hyeeyoung.wishboard.domain.util

import com.hyeeyoung.wishboard.domain.model.wish.BulkDeleteScope

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

    return if (isFullyLoaded && isMajoritySelected) {
        BulkDeleteTarget(
            scope = BulkDeleteScope.ALL,
            excludeItemIds = allLoadedItemIds.filterNot { selectedItemIds.contains(it) },
        )
    } else {
        BulkDeleteTarget(scope = BulkDeleteScope.SELECTED, itemIds = selectedItemIds.toList())
    }
}
