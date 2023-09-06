package com.hyeeyoung.wishboard.presentation.model

import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import java.time.LocalDateTime

data class WishItemDetail(
    val id: Long,
    val name: String,
    var image: String,
    val price: Int,
    val notiDate: LocalDateTime? = null,
    val notiType: NotiType? = null,
    val site: String? = null,
    val memo: String? = null,
    var folderId: Long? = null,
    var folderName: String? = null,
    val createAt: String,
)
