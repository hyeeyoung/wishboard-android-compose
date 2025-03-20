package com.hyeeyoung.wishboard.config

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalState {
    val isExpiredAuthLogin: MutableStateFlow<Boolean> = MutableStateFlow(false)
}