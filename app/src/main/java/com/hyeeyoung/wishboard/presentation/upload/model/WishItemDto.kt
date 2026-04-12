package com.hyeeyoung.wishboard.presentation.upload.model

import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemImage
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.util.safeValueOf
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
    @SerialName("itemStatus")
    val itemOwnershipStatus: String,
) {
    fun toDomain(): WishItem = WishItem(
        id = id,
        name = name,
        imageUrl = image.firstOrNull()?.url,
        price = price,
        itemOwnershipStatus = safeValueOf<WishItemOwnershipStatus>(itemOwnershipStatus) ?: WishItemOwnershipStatus.WISH,
    )
}
