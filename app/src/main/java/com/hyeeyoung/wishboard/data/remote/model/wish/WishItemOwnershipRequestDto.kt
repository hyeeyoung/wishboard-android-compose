package com.hyeeyoung.wishboard.data.remote.model.wish

import kotlinx.serialization.Serializable

@Serializable
data class WishItemOwnershipRequestDto(
    val status: String,
)
