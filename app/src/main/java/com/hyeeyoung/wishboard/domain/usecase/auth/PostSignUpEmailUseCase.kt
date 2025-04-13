package com.hyeeyoung.wishboard.domain.usecase.auth

import com.hyeeyoung.wishboard.domain.repository.AuthRepository
import javax.inject.Inject

class PostSignUpEmailUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> =
        repository.checkRegisteredUser(email)
}
