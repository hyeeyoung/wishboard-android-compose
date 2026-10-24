package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import com.hyeeyoung.wishboard.domain.model.user.UserProfile
import com.hyeeyoung.wishboard.domain.model.user.WithdrawalReason

interface UserRepository {
    suspend fun fetchUserInfo(): Result<UserInfo>

    suspend fun getWithdrawalReasons(): Result<List<WithdrawalReason>>

    suspend fun submitWithdrawalFeedback(reasonCode: String, detail: String?): Result<Unit>

    suspend fun updateUserInfo(
        userProfile: UserProfile,
    ): Result<Unit>

    suspend fun updatePushState(isPushAllowed: Boolean): Result<Unit>

    suspend fun changePassword(password: String): Result<Unit>

    suspend fun deleteUserAccount(): Result<Unit>
}
