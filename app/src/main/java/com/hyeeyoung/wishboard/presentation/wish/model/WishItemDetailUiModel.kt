package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.applyFormat
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class WishItemDetailUiModel(
    val id: Long = 0L,
    var folderId: Long? = 0L,
    var folderName: String? = "",
    var image: String? = null,
    val memo: String? = null,
    val name: String = "",
    val notiDate: LocalDateTime? = null,
    val notiType: NotiType? = null,
    val price: Long = 0L,
    val site: String? = null,
    val createAt: String = "",
) {
    companion object {
        fun fromDomain(domain: WishItemDetail): WishItemDetailUiModel =
            WishItemDetailUiModel(
                id = domain.id,
                folderId = domain.folderId,
                folderName = domain.folderName,
                image = domain.image,
                memo = domain.memo,
                name = domain.name,
                notiDate = domain.notiDate?.applyFormat(WishBoardDateFormat.YYYY_MM_DD_HH_MM_SS),
                notiType = domain.notiType,
                price = domain.price.toLongOrNull() ?: 0,
                site = domain.site,
                createAt = domain.createAt
            )
    }
}
