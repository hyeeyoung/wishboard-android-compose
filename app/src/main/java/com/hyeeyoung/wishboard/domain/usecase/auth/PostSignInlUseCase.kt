package com.hyeeyoung.wishboard.domain.usecase.auth

import com.hyeeyoung.wishboard.domain.model.auth.AuthInfo
import com.hyeeyoung.wishboard.domain.repository.AuthRepository
import javax.inject.Inject

class PostSignInlUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(authInfo: AuthInfo): Result<Unit> =
        repository.signIn(authInfo)
}
