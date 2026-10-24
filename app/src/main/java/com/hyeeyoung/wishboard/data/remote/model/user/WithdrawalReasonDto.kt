package com.hyeeyoung.wishboard.data.remote.model.user

import com.hyeeyoung.wishboard.domain.model.user.WithdrawalReason
import kotlinx.serialization.Serializable

@Serializable
data class WithdrawalReasonDto(
    val code: String,
    val description: String,
    val detailRequired: Boolean,
) {
    fun toDomain() = WithdrawalReason(
        code = code,
        description = description,
        detailRequired = detailRequired,
    )
}
