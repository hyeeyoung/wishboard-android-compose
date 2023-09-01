package com.hyeeyoung.wishboard.presentation.model

import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import java.time.LocalDateTime

data class Noti(
    val itemId: Long,
    val itemName: String,
    val itemImage: String,
    val type: NotiType,
    val date: LocalDateTime,
    val isRead: Boolean,
)
