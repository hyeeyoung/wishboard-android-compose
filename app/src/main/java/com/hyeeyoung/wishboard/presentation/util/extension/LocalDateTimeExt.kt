package com.hyeeyoung.wishboard.presentation.util.extension

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime

/** 날짜를 "a h시 mm분" 포맷으로 변경 */
fun LocalDateTime.getScheduleTimeFormat(): String {
    val isAM = if (this.hour < 12) "오전" else "오후"
    val hour = "${this.hour}시"
    val minute = if (this.minute == 0) "" else "${this.minute}분"

    return "$isAM $hour $minute"
}

// TODO 추후 삭제 또는 다른 파일로 이동
private const val ZONE_ID = "Asia/Seoul"
fun getCurrentTime() = Clock.System.now().toLocalDateTime(TimeZone.of(ZONE_ID))
