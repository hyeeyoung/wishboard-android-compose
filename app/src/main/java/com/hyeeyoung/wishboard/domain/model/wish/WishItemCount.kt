package com.hyeeyoung.wishboard.domain.model.wish

import kotlinx.serialization.Serializable

@Serializable
data class WishItemCount(
    val totalCount: Int,
    val ownedCount: Int,
)
