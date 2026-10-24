package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus

enum class WishListFilterType(val label: String) {
    ALL("전체"),
    OWNED("소장템만"),
    WISH("위시템만"),
}

fun WishListFilterType.toItemStatus(): WishItemOwnershipStatus? = when (this) {
    WishListFilterType.ALL -> null
    WishListFilterType.OWNED -> WishItemOwnershipStatus.OWNED
    WishListFilterType.WISH -> WishItemOwnershipStatus.WISH
}
