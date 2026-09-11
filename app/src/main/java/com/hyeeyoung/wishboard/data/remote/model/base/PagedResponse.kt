package com.hyeeyoung.wishboard.data.remote.model.base

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val totalPages: Int,
    val totalElements: Int,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val content: List<T>,
    val number: Int,
)
