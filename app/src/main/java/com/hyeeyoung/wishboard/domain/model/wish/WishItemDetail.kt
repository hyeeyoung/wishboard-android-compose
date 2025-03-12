package com.hyeeyoung.wishboard.domain.model.wish

import com.hyeeyoung.wishboard.domain.model.noti.NotiType

data class WishItemDetail(
    val id: Long,
    var folderId: Long?,
    var folderName: String?,
    var image: String?,
    val memo: String?,
    val name: String,
    val notiDate: String?,
    val notiType: NotiType?,
    val price: String,
    val site: String?,
    val createAt: String,
)
