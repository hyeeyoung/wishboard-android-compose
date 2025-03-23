package com.hyeeyoung.wishboard.domain.usecase.noti

import com.hyeeyoung.wishboard.domain.repository.NotiRepository
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import javax.inject.Inject

class PutNotiReadStateUseCase @Inject constructor(
    private val repository: NotiRepository,
) {
    suspend operator fun invoke(itemId: Long): Result<Unit> =
        repository.updateNotiReadState(itemId)
}
