package com.hyeeyoung.wishboard.data.remote.model.system

import com.hyeeyoung.wishboard.domain.model.system.AppVersion
import kotlinx.serialization.Serializable

@Serializable
data class AppVersionDto(
    val platform: String,
    val minVersion: String,
    val recommendedVersion: String,
) {
    fun toDomain(): AppVersion = AppVersion(
        minVersionCode = minVersion.toIntOrNull() ?: 0,
        latestVersionCode = recommendedVersion.toIntOrNull() ?: 0
    )
}
