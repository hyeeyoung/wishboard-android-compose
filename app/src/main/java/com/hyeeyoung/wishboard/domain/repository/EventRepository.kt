package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.noti.UnreadStatus
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    val unreadStatus: Flow<UnreadStatus>

    fun connect()
}
