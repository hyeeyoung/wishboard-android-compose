package com.hyeeyoung.wishboard.domain.usecase.user

import com.hyeeyoung.wishboard.domain.model.noti.UserInfo
import com.hyeeyoung.wishboard.domain.repository.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(): Result<UserInfo> =
        repository.fetchUserInfo()
}
