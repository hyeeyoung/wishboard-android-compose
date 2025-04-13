package com.hyeeyoung.wishboard.domain.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object WishBoardDateFormat {
    const val YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val YYYY_MM_DD_T_HH_MM = "yyyy-MM-dd'T'HH:mm"
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val YY_M_D_KR = "yy년 M월 d일"
    const val YY_M_D_HH_MM_KR = "$YY_M_D_KR HH:mm"
    const val YYYYMMDD_T_HHMMSS_Z = "yyyyMMdd'T'HHmmss'Z'"
    const val YY_M_D_A_H_MM = "yy. M. d a h:mm"

    fun String.toLocalDateTime(pattern: String): LocalDateTime? =
        runCatching {
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.KOREA)
            val localDateTime = java.time.LocalDateTime.parse(this, formatter)
            localDateTime.toKotlinLocalDateTime()
        }.getOrNull()

    fun LocalDateTime.getFormattedDateStr(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.KOREA)
        return this.toJavaLocalDateTime().format(formatter)
    }

    fun LocalDate.getFormattedDateStr(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.KOREA)
        return this.format(formatter)
    }

    fun kotlinx.datetime.LocalDate.getFormattedDateStr(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.KOREA)
        return this.toJavaLocalDate().format(formatter)
    }
}
