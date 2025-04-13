package com.hyeeyoung.wishboard.data.remote.repository

import com.hyeeyoung.wishboard.data.remote.service.SystemService
import com.hyeeyoung.wishboard.domain.model.system.AppVersion
import com.hyeeyoung.wishboard.domain.repository.SystemRepository
import javax.inject.Inject

class SystemRepositoryImpl @Inject constructor(
    private val systemService: SystemService,
) : SystemRepository {
    override suspend fun getAppVersion(): Result<AppVersion> = runCatching {
        systemService.fetchAppVersion().data
    }.map { it.toDomain() }
}
