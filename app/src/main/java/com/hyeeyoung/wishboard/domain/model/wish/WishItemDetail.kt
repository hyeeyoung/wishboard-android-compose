package com.hyeeyoung.wishboard.domain.model.wish

import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import kotlinx.serialization.Serializable

@Serializable
data class WishItemDetail(
    val id: Long,
    var folderId: Long?,
    var folderName: String?,
    var images: List<String>,
    val memo: String?,
    val name: String,
    val notiDate: String?,
    val notiType: NotiType?,
    val price: String,
    val site: String?,
    val createAt: String,
    val version: Int,
)
