package com.hyeeyoung.wishboard.config

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalState {
    val isExpiredAuthLogin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var previousBottomBarRoute: MutableStateFlow<String?> = MutableStateFlow(null)
    var reselectedBottomBarRoute: MutableStateFlow<String?> = MutableStateFlow(null)
}
