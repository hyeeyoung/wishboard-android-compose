package com.hyeeyoung.wishboard.presentation.noti

import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem

data class NotiListUiModel(
    val notiList: List<NotiItem> = emptyList(),
    val isRefreshing: Boolean = false,
)
