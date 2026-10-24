package com.hyeeyoung.wishboard.domain.usecase.event

import com.hyeeyoung.wishboard.domain.model.noti.UnreadStatus
import com.hyeeyoung.wishboard.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUnreadStatusUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke(): Flow<UnreadStatus> = repository.unreadStatus
}
