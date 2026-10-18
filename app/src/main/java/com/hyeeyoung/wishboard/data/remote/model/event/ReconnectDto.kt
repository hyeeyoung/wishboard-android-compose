package com.hyeeyoung.wishboard.data.remote.model.event

import kotlinx.serialization.Serializable

@Serializable
data class ReconnectDto(
    val reason: String = "",
)
