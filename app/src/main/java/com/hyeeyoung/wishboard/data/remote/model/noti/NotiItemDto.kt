package com.hyeeyoung.wishboard.data.remote.model.noti

import com.hyeeyoung.wishboard.domain.model.noti.NotiType.Companion.toNotiType
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.applyFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotiItemDto(
    @SerialName("item_id")
    val itemId: Long,
    @SerialName("item_img_url")
    val itemImage: String? = null,
    @SerialName("item_name")
    val itemName: String,
    @SerialName("item_url")
    val itemUrl: String? = null,
    @SerialName("read_state")
    var readState: Int,
    @SerialName("item_notification_type")
    val notiType: String,
    @SerialName("item_notification_date")
    val notiDate: String,
) {
    fun toDomain() = NotiItem(
        itemId = itemId,
        itemImage = itemImage,
        itemName = itemName,
        itemUrl = if (itemUrl.isNullOrBlank()) null else itemUrl,
        isRead = readState != 0,
        notiType = notiType.toNotiType(),
        notiDate = notiDate.applyFormat(WishBoardDateFormat.YYYY_MM_DD_HH_MM_SS),
    )
}