package com.hyeeyoung.wishboard.domain.model.noti

data class UnreadStatus(
    val hasUnread: Boolean = false,
    val unreadCount: Int = 0,
)
