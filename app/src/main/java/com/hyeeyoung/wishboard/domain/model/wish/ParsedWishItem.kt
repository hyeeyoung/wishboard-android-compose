package com.hyeeyoung.wishboard.domain.model.wish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParsedWishItem(
    @SerialName("itemImageUrl")
    val image: String? = null,
    @SerialName("itemName")
    val name: String? = null,
    @SerialName("itemPrice")
    val price: String? = null,
)
