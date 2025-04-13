package com.hyeeyoung.wishboard.data.remote.model.wish

import com.hyeeyoung.wishboard.domain.model.noti.NotiType.Companion.toNotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishItemDetailDto(
    @SerialName("item_id")
    val id: Long,
    @SerialName("folder_id")
    val folderId: Long?,
    @SerialName("folder_name")
    val folderName: String?,
    @SerialName("item_img_url")
    val image: String?,
    @SerialName("item_name")
    val name: String,
    @SerialName("item_price")
    val price: String,
    @SerialName("item_url")
    val site: String?,
    @SerialName("item_memo")
    val memo: String?,
    @SerialName("item_notification_date")
    val notiDate: String?,
    @SerialName("item_notification_type")
    val notiType: String?,
    @SerialName("create_at")
    val createAt: String,
) {
    fun toDomain() = WishItemDetail(
        id = id,
        folderId = folderId,
        folderName = if (folderName.isNullOrBlank()) null else folderName,
        image = image,
        memo = if (memo.isNullOrBlank()) null else memo,
        name = name,
        notiDate = notiDate,
        notiType = notiType?.toNotiType(),
        price = price,
        site = if (site.isNullOrBlank()) null else site,
        createAt = createAt,
    )
}
