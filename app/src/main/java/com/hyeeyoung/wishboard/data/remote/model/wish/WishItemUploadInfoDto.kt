package com.hyeeyoung.wishboard.data.remote.model.wish

import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.presentation.util.extension.isEmptyOrBlank
import kotlinx.serialization.Serializable

@Serializable
data class WishItemUploadInfoDto(
    val folderId: Long? = null,
    val itemName: String,
    val itemPrice: Int? = null,
    val itemUrl: String? = null,
    val itemNotificationType: String? = null,
    val itemNotificationDate: String? = null,
    val itemMemo: String? = null,
) {
    companion object {
        fun fromDomain(domain: WishItemUploadInfo) = WishItemUploadInfoDto(
            folderId = domain.folderId,
            itemName = domain.itemName,
            itemPrice = domain.itemPrice,
            itemUrl = domain.itemUrl,
            itemNotificationType = domain.itemNotiType?.name,
            itemNotificationDate = domain.itemNotiDate,
            itemMemo = if (domain.itemMemo?.isEmptyOrBlank() == true) null else domain.itemMemo,
        )
    }
}
