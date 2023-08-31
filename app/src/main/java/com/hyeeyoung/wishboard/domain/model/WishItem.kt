package com.hyeeyoung.wishboard.domain.model

data class WishItem( // TODO presentation > model 패키지로 이동
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
    var isInCart: Boolean,
)
