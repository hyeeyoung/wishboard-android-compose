package com.hyeeyoung.wishboard.domain.usecase.auth

import com.hyeeyoung.wishboard.domain.repository.AuthRepository
import javax.inject.Inject

class PostLogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        repository.logout()
}
