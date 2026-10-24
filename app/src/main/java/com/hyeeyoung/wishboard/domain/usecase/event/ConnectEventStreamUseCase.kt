package com.hyeeyoung.wishboard.domain.usecase.event

import com.hyeeyoung.wishboard.domain.repository.EventRepository
import javax.inject.Inject

class ConnectEventStreamUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke() = repository.connect()
}
