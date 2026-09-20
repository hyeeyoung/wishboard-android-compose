package com.hyeeyoung.wishboard.domain.model.noti

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class NotiInfo(
    val notiType: NotiType? = null,
    val notiDate: LocalDateTime? = null,
)
