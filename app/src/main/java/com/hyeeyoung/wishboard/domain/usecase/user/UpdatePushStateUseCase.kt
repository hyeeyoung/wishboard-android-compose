package com.hyeeyoung.wishboard.domain.usecase.user

import com.hyeeyoung.wishboard.domain.repository.UserRepository
import javax.inject.Inject

class UpdatePushStateUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(isPushAllowed: Boolean): Result<Unit> =
        repository.updatePushState(isPushAllowed = isPushAllowed)
}
