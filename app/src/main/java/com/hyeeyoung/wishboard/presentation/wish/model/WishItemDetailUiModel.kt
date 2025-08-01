package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.toLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class WishItemDetailUiModel(
    val id: Long = 0L,
    var folderId: Long? = 0L,
    var folderName: String? = "",
    var images: List<String> = emptyList(),
    val memo: String? = null,
    val name: String = "",
    val notiDate: LocalDateTime? = null,
    val notiType: NotiType? = null,
    val price: Long = 0L,
    val site: String? = null,
    val createAt: LocalDateTime? = null,
) {
    companion object {
        fun fromDomain(domain: WishItemDetail): WishItemDetailUiModel =
            WishItemDetailUiModel(
                id = domain.id,
                folderId = domain.folderId,
                folderName = domain.folderName,
                images = domain.image ?: emptyList(),
                memo = domain.memo,
                name = domain.name,
                notiDate = with(domain.notiDate) {
                    val date1 = this?.toLocalDateTime(WishBoardDateFormat.YYYY_MM_DD_HH_MM)
                    date1 ?: this?.toLocalDateTime(WishBoardDateFormat.YYYY_MM_DD_HH_MM_SS)
                },
                notiType = domain.notiType,
                price = domain.price.toLongOrNull() ?: 0,
                site = domain.site,
                createAt = domain.createAt.toLocalDateTime(WishBoardDateFormat.YYYY_MM_DD_HH_MM_SS),
            )
    }
}
