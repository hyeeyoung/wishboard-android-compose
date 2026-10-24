package com.hyeeyoung.wishboard.data.remote.repository

import com.hyeeyoung.wishboard.data.remote.sse.EventSseClient
import com.hyeeyoung.wishboard.domain.model.noti.UnreadStatus
import com.hyeeyoung.wishboard.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventSseClient: EventSseClient,
) : EventRepository {
    override val unreadStatus: Flow<UnreadStatus> = eventSseClient.unreadStatus

    override fun connect() = eventSseClient.start()
}
