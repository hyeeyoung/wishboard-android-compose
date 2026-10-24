package com.hyeeyoung.wishboard.data.remote.model.wish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateBulkItemStatusRequestDto(
    @SerialName("status")
    val targetStatus: String,
    val itemIds: List<Long>? = null,
)
