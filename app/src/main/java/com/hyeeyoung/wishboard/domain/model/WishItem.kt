package com.hyeeyoung.wishboard.domain.model

data class WishItem(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
    var isInCart: Boolean,
)
