package com.hyeeyoung.wishboard.data.remote.model.user

import kotlinx.serialization.Serializable

@Serializable
data class WithdrawalFeedbackDto(
    val reason: String,
    val detail: String? = null,
)
