package com.hyeeyoung.wishboard.data.remote.model.base

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponseWithoutData(
    val success: Boolean,
    val message: String,
)
