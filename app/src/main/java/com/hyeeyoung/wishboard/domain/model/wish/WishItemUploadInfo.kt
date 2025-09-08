package com.hyeeyoung.wishboard.domain.model.wish

import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.presentation.common.model.ImageType

data class WishItemUploadInfo(
    val folderId: Long? = null,
    val itemName: String,
    val itemPrice: Int? = null,
    val itemUrl: String? = null,
    val itemNotiType: NotiType? = null,
    val itemNotiDate: String? = null,
    val itemImage: List<ImageType>?,
    val itemMemo: String? = null,
    /** 아이템 수정 한정으로 필요한 프로퍼티 */
    val updateInfo: UpdateInfo? = null,
) {
    data class UpdateInfo(
        val version: Int,
        val imageChanged: Boolean,
    )
}
