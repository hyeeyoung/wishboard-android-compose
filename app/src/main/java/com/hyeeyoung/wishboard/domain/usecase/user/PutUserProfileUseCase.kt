package com.hyeeyoung.wishboard.domain.usecase.user

import com.hyeeyoung.wishboard.domain.model.user.UserProfile
import com.hyeeyoung.wishboard.domain.repository.UserRepository
import javax.inject.Inject

class PutUserProfileUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(userProfile: UserProfile): Result<Unit> =
        repository.updateUserInfo(userProfile)
}
