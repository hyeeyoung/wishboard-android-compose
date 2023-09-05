package com.hyeeyoung.wishboard.presentation.util.extension

import java.time.LocalDateTime
import java.time.temporal.ChronoField

/** 날짜를 "a h시 mm분" 포맷으로 변경 */
fun LocalDateTime.getScheduleTimeFormat(): String {
    val ampm = if (this.get(ChronoField.AMPM_OF_DAY) == 0) "오전" else "오후"
    val hour = "${this.get(ChronoField.CLOCK_HOUR_OF_AMPM)}시"
    val minute = if (this.minute == 0) "" else "${this.minute}분"

    return "$ampm $hour $minute"
}
