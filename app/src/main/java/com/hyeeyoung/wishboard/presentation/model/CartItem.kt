package com.hyeeyoung.wishboard.presentation.model

data class CartItem(
    val id: Long,
    val name: String,
    val image: String,
    val price: Long,
    var count: Int,
)
