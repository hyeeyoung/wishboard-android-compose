package com.hyeeyoung.wishboard.data.remote.model.wish

import kotlinx.serialization.Serializable

@Serializable
data class DeleteBulkItemsRequestDto(
    val itemIds: List<Long>? = null,
    val excludeItemIds: List<Long>? = null,
)
