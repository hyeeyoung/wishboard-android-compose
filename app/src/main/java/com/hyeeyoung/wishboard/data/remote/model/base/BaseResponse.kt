package com.hyeeyoung.wishboard.data.remote.model.base

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val code: String = "",
    val data: T,
)
