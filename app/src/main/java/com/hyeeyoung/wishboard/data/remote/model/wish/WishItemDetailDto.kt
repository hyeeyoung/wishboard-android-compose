package com.hyeeyoung.wishboard.data.remote.model.wish

import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.util.safeValueOf
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
    val version: Int,
    @SerialName("itemStatus")
    val itemStatus: String,
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
        version = version,
        isOwnedItem = when (safeValueOf<WishItemOwnershipStatus>(itemStatus)) {
            WishItemOwnershipStatus.OWNED -> true
            else -> false
        },
    )
}
