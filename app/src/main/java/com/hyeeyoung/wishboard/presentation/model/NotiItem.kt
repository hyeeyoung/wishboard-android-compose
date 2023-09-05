package com.hyeeyoung.wishboard.presentation.model

import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import java.time.LocalDateTime

data class NotiItem(
    val itemId: Long,
    val itemImg: String? = null,
    val itemName: String,
    val itemUrl: String? = null,
    var readState: Int,
    val notiType: NotiType,
    val notiDate: LocalDateTime,
)
