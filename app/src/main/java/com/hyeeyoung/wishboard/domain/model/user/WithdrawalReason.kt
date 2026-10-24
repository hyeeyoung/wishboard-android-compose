package com.hyeeyoung.wishboard.domain.model.user

data class WithdrawalReason(
    val code: String,
    val description: String,
    val detailRequired: Boolean,
)
