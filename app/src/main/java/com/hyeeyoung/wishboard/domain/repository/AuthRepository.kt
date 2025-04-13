package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.auth.AuthInfo

interface AuthRepository {
    suspend fun signUp(authInfo: AuthInfo): Result<Unit>
    suspend fun signIn(authInfo: AuthInfo): Result<Unit>
    suspend fun requestVerificationMail(email: String): Result<String>
    suspend fun signInEmail(authInfo: AuthInfo): Result<Unit>
    suspend fun checkRegisteredUser(email: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}
