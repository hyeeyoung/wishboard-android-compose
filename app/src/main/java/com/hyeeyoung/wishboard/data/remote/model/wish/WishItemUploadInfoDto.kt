package com.hyeeyoung.wishboard.data.remote.model.wish

import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import kotlinx.serialization.Serializable

@Serializable
data class WishItemUploadInfoDto(
    val folderId: Long? = null,
    val itemName: String,
    val itemPrice: Int? = null,
    val itemUrl: String? = null,
    val itemNotiDate: String? = null,
    val itemNotiType: String? = null,
    val itemMemo: String? = null,
) {
    companion object {
        fun fromDomain(domain: WishItemUploadInfo) = WishItemUploadInfoDto(
            folderId = domain.folderId,
            itemName = domain.itemName,
            itemPrice = domain.itemPrice,
            itemUrl = domain.itemUrl,
            itemNotiDate = domain.itemNotiDate,
            itemNotiType = domain.itemNotiType?.name,
            itemMemo = domain.itemMemo,
        )
    }
}
