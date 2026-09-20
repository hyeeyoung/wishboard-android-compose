package com.hyeeyoung.wishboard.config

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalState {
    val autoLoginExpiryInfo: MutableStateFlow<Pair<Boolean, String>> = MutableStateFlow(false to "") // TODO 데이터타입 변경 필요
    var previousBottomBarRoute: MutableStateFlow<String?> = MutableStateFlow(null)
    var reselectedBottomBarRoute: MutableStateFlow<String?> = MutableStateFlow(null)
}
