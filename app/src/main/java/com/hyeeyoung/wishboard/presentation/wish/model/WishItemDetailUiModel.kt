package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.data.util.extension.toInstantToLocalDateTime
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class WishItemDetailUiModel(
    val id: Long = 0L,
    var folderId: Long? = null,
    var folderName: String? = "",
    var images: List<String> = emptyList(),
    val memo: String? = null,
    val name: String = "",
    val notiDate: LocalDateTime? = null,
    val notiType: NotiType? = null,
    val price: Long = 0L,
    val site: String? = null,
    val createAt: LocalDateTime? = null,
    val version: Int = 0,
) {
    companion object {
        fun fromDomain(domain: WishItemDetail): WishItemDetailUiModel =
            WishItemDetailUiModel(
                id = domain.id,
                folderId = domain.folderId,
                folderName = domain.folderName,
                images = domain.images,
                memo = domain.memo,
                name = domain.name,
                notiDate = domain.notiDate?.toInstantToLocalDateTime(),
                notiType = domain.notiType,
                price = domain.price.toLongOrNull() ?: 0,
                site = domain.site,
                createAt = domain.createAt.toInstantToLocalDateTime(),
                version = domain.version,
            )
    }
}
