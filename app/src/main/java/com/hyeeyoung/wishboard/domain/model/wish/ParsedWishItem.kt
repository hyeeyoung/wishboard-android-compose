package com.hyeeyoung.wishboard.domain.model.wish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParsedWishItem(
    @SerialName("item_img")
    val image: String? = null,
    @SerialName("item_name")
    val name: String? = null,
    @SerialName("item_price")
    val price: String? = null,
)
