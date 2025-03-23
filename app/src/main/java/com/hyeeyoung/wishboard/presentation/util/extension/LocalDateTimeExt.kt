package com.hyeeyoung.wishboard.presentation.util.extension

import com.hyeeyoung.wishboard.presentation.util.TimeUtil
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** 날짜를 "a h시 mm분" 포맷으로 변경 */
fun LocalDateTime.getScheduleTimeFormat(): String {
    val isAM = if (this.hour < 12) "오전" else "오후"

    val formatter = DateTimeFormatter.ofPattern("h시")
    val hour = this.toJavaLocalDateTime().format(formatter) // 12시간 형식

    val minute = if (this.minute == 0) "" else "${this.minute}분"

    return "$isAM $hour $minute"
}

fun LocalDateTime.toNotiDateStr(): String {
    val year = this.year % 100  // 2022 → 22
    val month = this.monthNumber
    val day = this.dayOfMonth
    val hour = this.hour
    val minute = this.minute

    return if (minute == 0) {
        "%02d년 %d월 %d일 %d시".format(year, month, day, hour)
    } else {
        "%02d년 %d월 %d일 %d시 %d분".format(year, month, day, hour, minute)
    }
}

// TODO 추후 삭제 또는 다른 파일로 이동
private const val ZONE_ID = "Asia/Seoul"
fun getCurrentTime() = Clock.System.now().toLocalDateTime(TimeZone.of(ZONE_ID))

fun LocalDateTime.formatAsTimeAgo(): String {
    val regTime = this.toJavaLocalDateTime().atZone(ZoneId.of(ZONE_ID)).toInstant().toEpochMilli()
    val curTime = System.currentTimeMillis()
    var diffTime = (curTime - regTime) / 1000

    return when {
        diffTime < TimeUtil.SEC -> "방금 전"

        TimeUtil.SEC.let { diffTime /= it; diffTime } < TimeUtil.MIN -> "${diffTime}분 전"

        TimeUtil.MIN.let { diffTime /= it; diffTime } < TimeUtil.HOUR -> "${diffTime}시간 전"

        TimeUtil.HOUR.let { diffTime /= it; diffTime } < TimeUtil.DAY_OF_MONTH ->
            if (diffTime < TimeUtil.DAY_OF_WEEK) {
                "${diffTime}일 전"
            } else {
                diffTime /= TimeUtil.DAY_OF_WEEK
                "${diffTime}주 전"
            }

        TimeUtil.DAY_OF_MONTH.let { diffTime /= it; diffTime } < TimeUtil.MONTH -> "${diffTime}개월 전"

        else -> {
            diffTime /= TimeUtil.MONTH
            "${diffTime}년 전"
        }
    }
}
