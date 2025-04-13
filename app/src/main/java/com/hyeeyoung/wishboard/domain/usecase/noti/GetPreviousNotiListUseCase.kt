package com.hyeeyoung.wishboard.domain.usecase.noti

import com.hyeeyoung.wishboard.domain.repository.NotiRepository
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import javax.inject.Inject

class GetPreviousNotiListUseCase @Inject constructor(
    private val repository: NotiRepository,
) {
    suspend operator fun invoke(): Result<List<NotiItem>> =
        repository.fetchPreviousNotiList()
}
