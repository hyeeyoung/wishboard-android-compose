package com.hyeeyoung.wishboard.presentation.upload.model

import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemImage
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishItemDto(
    @SerialName("id")
    val id: Long,
    @SerialName("itemImages")
    var image: List<WishItemImage> = emptyList(), // TODO 이미지 없을 때 테스트
    @SerialName("itemUrl")
    var site: String? = null,
    @SerialName("itemName")
    val name: String,
    @SerialName("itemPrice")
    val price: Long? = null,
) {
    fun toDomain(): WishItem = WishItem(
        id = id,
        name = name,
        imageUrl = image.firstOrNull()?.url,
        price = price,
    )
}
