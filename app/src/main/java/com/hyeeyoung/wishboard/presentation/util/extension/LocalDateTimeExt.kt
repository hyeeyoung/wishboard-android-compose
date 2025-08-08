package com.hyeeyoung.wishboard.presentation.util.extension

import com.hyeeyoung.wishboard.presentation.util.TimeUtil
import com.hyeeyoung.wishboard.presentation.util.TimeUtil.KOREA_ZONE_ID
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

/** 날짜를 "a h시 mm분" 포맷으로 변경 */
fun LocalDateTime.getScheduleTimeFormat(): String {
    val isAM = if (this.hour < 12) "오전" else "오후"

    val formatter = DateTimeFormatter.ofPattern("h시")
    val hour = this.toJavaLocalDateTime().format(formatter) // 12시간 형식

    val minute = if (this.minute == 0) "" else "${this.minute}분"

    return "$isAM $hour $minute"
}

fun LocalDateTime.toNotiDateStr(): String {
    val year = this.year % 100 // 2022 → 22
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
fun getCurrentTime() = Clock.System.now().toLocalDateTime(TimeZone.of(KOREA_ZONE_ID))

fun LocalDateTime.formatAsTimeAgo(): String {
    val regTime = this.toJavaLocalDateTime().atZone(ZoneId.of(KOREA_ZONE_ID)).toInstant().toEpochMilli()
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

/** 디데이 계산 */
fun LocalDateTime.formatDday(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val targetDate = this.date
    val targetTime = this.time

    val daysDiff = targetDate.daysUntil(now.date)

    return when {
        daysDiff < -1 -> "D-${abs(daysDiff)}" // 디데이까지 이틀 이상 남음
        daysDiff == -1 -> formatTime("내일 ", targetTime) // 디데이 하루 전
        daysDiff == 0 -> formatTime("오늘 ", targetTime) // 디데이 당일
        else -> "${targetDate.year % 100}년 ${targetDate.monthNumber}월 ${targetDate.dayOfMonth}일" // 지난 날짜
    }
}

// 시간을 "오늘 13시 30분" 혹은 "오늘 13시" 형식으로 변환
private fun formatTime(prefix: String, time: LocalTime): String {
    return if (time.minute == 0) {
        "$prefix ${time.hour}시"
    } else {
        "$prefix ${time.hour}시 ${time.minute}분"
    }
}
