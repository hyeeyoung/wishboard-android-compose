package com.hyeeyoung.wishboard.presentation.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object WishBoardDateFormat {
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val YY_M_D_KR = "yy년 M월 d일"
    const val YYYYMMDD_T_HHMMSS_Z = "yyyyMMdd'T'HHmmss'Z'"

    fun String.applyFormat(pattern: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.KOREA)
        val localDateTime = java.time.LocalDateTime.parse(this, formatter)
        return localDateTime.toKotlinLocalDateTime()
    }

    fun LocalDateTime.getFormattedDateStr(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return this.toJavaLocalDateTime().format(formatter)
    }
}
