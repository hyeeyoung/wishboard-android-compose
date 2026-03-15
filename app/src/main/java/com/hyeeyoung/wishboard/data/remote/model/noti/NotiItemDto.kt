package com.hyeeyoung.wishboard.data.remote.model.noti

import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemImage
import com.hyeeyoung.wishboard.data.util.extension.toInstantToLocalDateTime
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotiItemDto(
    @SerialName("id")
    val itemId: Long,
    @SerialName("itemImages")
    val itemImage: List<WishItemImage> = emptyList(),
    @SerialName("itemName")
    val itemName: String,
    @SerialName("itemUrl")
    val itemUrl: String? = null,
    @SerialName("readState")
    var isRead: Boolean,
    @SerialName("itemNotificationType")
    val notiType: String,
    @SerialName("itemNotificationDate")
    val notiDate: String,
) {
    fun toDomain() = NotiItem(
        itemId = itemId,
        itemImage = itemImage.firstOrNull()?.url,
        itemName = itemName,
        itemUrl = if (itemUrl.isNullOrBlank()) null else itemUrl,
        isRead = isRead,
        notiType = NotiType.fromDomain(notiType) ?: NotiType.OPEN,
        notiDate = notiDate.toInstantToLocalDateTime(),
    )
}
