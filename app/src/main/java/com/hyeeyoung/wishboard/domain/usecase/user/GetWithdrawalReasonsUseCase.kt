package com.hyeeyoung.wishboard.domain.usecase.user

import com.hyeeyoung.wishboard.domain.repository.UserRepository
import javax.inject.Inject

class GetWithdrawalReasonsUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke() = repository.getWithdrawalReasons()
}
