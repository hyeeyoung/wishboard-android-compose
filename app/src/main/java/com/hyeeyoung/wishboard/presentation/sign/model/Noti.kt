package com.hyeeyoung.wishboard.presentation.sign.model

import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import kotlinx.datetime.LocalDateTime


data class Noti(
    val itemId: Long,
    val itemName: String,
    val itemImage: String,
    val type: NotiType,
    val date: LocalDateTime,
    val isRead: Boolean,
    val site: String? = null,
)
