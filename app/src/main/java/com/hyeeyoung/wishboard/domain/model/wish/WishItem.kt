package com.hyeeyoung.wishboard.domain.model.wish

data class WishItem(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val price: Long?,
)
