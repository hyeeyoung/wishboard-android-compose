package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.system.AppVersion

interface SystemRepository {
    suspend fun getAppVersion(): Result<AppVersion>
}
