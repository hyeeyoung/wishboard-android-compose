package com.hyeeyoung.wishboard.presentation.util

import kotlinx.datetime.TimeZone

object TimeUtil {
    const val SEC = 60
    const val MIN = 60
    const val HOUR = 24
    const val DAY_OF_MONTH = 30
    const val DAY_OF_WEEK = 7
    const val MONTH = 12

    const val KOREA_ZONE_ID = "Asia/Seoul"
    val koreaTimeZone = TimeZone.of("Asia/Seoul")
}
