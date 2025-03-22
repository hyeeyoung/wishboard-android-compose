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
    val itemImage: ImageType?,
    val itemMemo: String? = null,
)
