package com.hyeeyoung.wishboard.presentation.upload.model

import com.hyeeyoung.wishboard.domain.model.WishItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishItemDto(
    @SerialName("item_id")
    val id: Long,
    @SerialName("item_img")
    var image: String? = null,
    @SerialName("item_url")
    var site: String? = null,
    @SerialName("item_name")
    val name: String,
    @SerialName("item_price")
    val price: Int? = null,
) {
    fun toDomain(): WishItem = WishItem(
        id = id, name = name, imageUrl = image, price = price,
    )
}
