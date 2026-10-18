package com.hyeeyoung.wishboard.data.remote.model.event

import com.hyeeyoung.wishboard.domain.model.noti.UnreadStatus
import kotlinx.serialization.Serializable

@Serializable
data class UnreadStatusDto(
    val hasUnread: Boolean = false,
    val unreadCount: Int = 0,
) {
    fun toDomain() = UnreadStatus(
        hasUnread = hasUnread,
        unreadCount = unreadCount,
    )
}
