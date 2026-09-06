package com.hyeeyoung.wishboard.domain.usecase.auth

import com.hyeeyoung.wishboard.domain.repository.AuthRepository
import javax.inject.Inject

class GetWebViewTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<String> = authRepository.getWebViewToken()
}
