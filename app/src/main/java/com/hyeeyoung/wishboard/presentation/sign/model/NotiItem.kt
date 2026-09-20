package com.hyeeyoung.wishboard.presentation.sign.model

import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import kotlinx.datetime.LocalDateTime

data class NotiItem(
    val itemId: Long,
    val itemImage: String? = null,
    val itemName: String,
    val itemUrl: String? = null,
    var isRead: Boolean,
    val notiType: NotiType,
    val notiDate: LocalDateTime?,
)
