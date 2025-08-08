package com.hyeeyoung.wishboard.data.remote.model.wish

import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishItemDetailDto(
    @SerialName("id")
    val id: Long,
    @SerialName("folderId")
    val folderId: Long?,
    @SerialName("folderName")
    val folderName: String?,
    @SerialName("itemImages")
    val image: List<WishItemImage>?,
    @SerialName("itemName")
    val name: String,
    @SerialName("itemPrice")
    val price: String,
    @SerialName("itemUrl")
    val site: String?,
    @SerialName("itemMemo")
    val memo: String?,
    @SerialName("itemNotificationDate")
    val notiDate: String?,
    @SerialName("itemNotificationType")
    val notiType: String?,
    @SerialName("createdAt")
    val createAt: String,
) {
    fun toDomain() = WishItemDetail(
        id = id,
        folderId = folderId,
        folderName = if (folderName.isNullOrBlank()) null else folderName,
        images = image?.map { it.url } ?: emptyList(),
        memo = if (memo.isNullOrBlank()) null else memo,
        name = name,
        notiDate = notiDate,
        notiType = NotiType.fromDomain(notiType),
        price = price,
        site = if (site.isNullOrBlank()) null else site,
        createAt = createAt,
    )
}
