package com.hyeeyoung.wishboard.domain.usecase.user

import com.hyeeyoung.wishboard.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserAccountUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        repository.deleteUserAccount()
}
