package com.hyeeyoung.wishboard.domain.usecase.user

import com.hyeeyoung.wishboard.domain.repository.UserRepository
import javax.inject.Inject

class SubmitWithdrawalFeedbackUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(reasonCode: String, detail: String?): Result<Unit> =
        repository.submitWithdrawalFeedback(reasonCode = reasonCode, detail = detail)
}
