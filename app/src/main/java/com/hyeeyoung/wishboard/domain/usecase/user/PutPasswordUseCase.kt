package com.hyeeyoung.wishboard.domain.usecase.user

import com.hyeeyoung.wishboard.domain.repository.UserRepository
import javax.inject.Inject

class PutPasswordUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(password: String): Result<Unit> =
        repository.changePassword(password = password)
}
