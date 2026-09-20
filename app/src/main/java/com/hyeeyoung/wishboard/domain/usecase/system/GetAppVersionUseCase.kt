package com.hyeeyoung.wishboard.domain.usecase.system

import com.hyeeyoung.wishboard.domain.model.system.AppVersion
import com.hyeeyoung.wishboard.domain.repository.SystemRepository
import javax.inject.Inject

class GetAppVersionUseCase @Inject constructor(
    private val repository: SystemRepository,
) {
    suspend operator fun invoke(): Result<AppVersion> =
        repository.getAppVersion()
}
