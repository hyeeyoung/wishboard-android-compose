package com.hyeeyoung.wishboard.presentation.noti.model

import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import java.time.LocalDate

data class CalendarUiModel(
    val schedules: List<NotiItem> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
)
