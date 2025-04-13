package com.hyeeyoung.wishboard.data.remote.repository

import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.remote.model.user.PasswordDto
import com.hyeeyoung.wishboard.data.remote.service.UserService
import com.hyeeyoung.wishboard.data.util.extension.toPlainNullableRequestBody
import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import com.hyeeyoung.wishboard.domain.model.user.UserProfile
import com.hyeeyoung.wishboard.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val localStorage: WishBoardPreference,
) : UserRepository {
    override suspend fun fetchUserInfo(): Result<UserInfo> = runCatching {
        val remoteUserInfo = userService.fetchUserInfo().firstOrNull()
        val nickName = if (remoteUserInfo?.nickname != null) {
            remoteUserInfo.nickname
        } else {
            localStorage.userInfo.nickname.ifBlank { null }
        }

        remoteUserInfo?.copy(nickname = nickName)?.toDomain() ?: UserInfo()
    }.onSuccess { localStorage.userInfo = it }

    override suspend fun updateUserInfo(userProfile: UserProfile): Result<Unit> = runCatching {
        userService.updateUserInfo(
            nickname = userProfile.nickName.toPlainNullableRequestBody(),
            profileImg = userProfile.profileImage,
        )
    }.onSuccess {
        localStorage.clear(WishBoardPreference.USER_INFO)
    }.map { }

    override suspend fun updatePushState(isPushAllowed: Boolean): Result<Unit> = runCatching {
        userService.updatePushState(isPushAllowed)
    }.onSuccess {
        localStorage.userInfo = localStorage.userInfo.copy(isPushAllowed = isPushAllowed)
    }.map { }

    override suspend fun changePassword(password: String): Result<Unit> = runCatching {
        userService.changePassword(PasswordDto(newPassword = password))
    }

    override suspend fun deleteUserAccount(): Result<Unit> = runCatching {
        userService.deleteUserAccount()
    }.onSuccess {
        localStorage.clear()
    }.map { }
}
