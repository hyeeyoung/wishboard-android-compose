package com.hyeeyoung.wishboard.data.remote.model.wish

import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.presentation.util.extension.isEmptyOrBlank
import kotlinx.serialization.Serializable

@Serializable
sealed class WishItemUploadInfoDto {
    abstract val folderId: Long?
    abstract val itemName: String
    abstract val itemPrice: Int?
    abstract val itemUrl: String?
    abstract val itemNotificationType: String?
    abstract val itemNotificationDate: String?
    abstract val itemMemo: String?

    @Serializable
    data class Parsing(
        override val folderId: Long? = null,
        override val itemName: String,
        override val itemPrice: Int? = null,
        override val itemUrl: String? = null,
        override val itemNotificationType: String? = null,
        override val itemNotificationDate: String? = null,
        override val itemMemo: String? = null,
    ) : WishItemUploadInfoDto()

    @Serializable
    data class Manual(
        override val folderId: Long? = null,
        override val itemName: String,
        override val itemPrice: Int? = null,
        override val itemUrl: String? = null,
        override val itemNotificationType: String? = null,
        override val itemNotificationDate: String? = null,
        override val itemMemo: String? = null,
        val version: Int,
        val imageChanged: Boolean,
    ) : WishItemUploadInfoDto()

    companion object {
        fun fromDomain(domain: WishItemUploadInfo): WishItemUploadInfoDto {
            val memo = if (domain.itemMemo?.isEmptyOrBlank() == true) null else domain.itemMemo
            return when (domain.updateInfo) {
                null -> Parsing(
                    folderId = domain.folderId,
                    itemName = domain.itemName,
                    itemPrice = domain.itemPrice,
                    itemUrl = domain.itemUrl,
                    itemNotificationType = domain.itemNotiType?.name,
                    itemNotificationDate = domain.itemNotiDate,
                    itemMemo = memo,
                )
                else -> Manual(
                    folderId = domain.folderId,
                    itemName = domain.itemName,
                    itemPrice = domain.itemPrice,
                    itemUrl = domain.itemUrl,
                    itemNotificationType = domain.itemNotiType?.name,
                    itemNotificationDate = domain.itemNotiDate,
                    itemMemo = memo,
                    version = domain.updateInfo.version,
                    imageChanged = domain.updateInfo.imageChanged,
                )
            }
        }
    }
}
