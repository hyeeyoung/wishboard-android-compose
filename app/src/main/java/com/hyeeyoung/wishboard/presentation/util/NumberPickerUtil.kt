package com.hyeeyoung.wishboard.presentation.util

import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.getFormattedDateStr
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object NumberPickerUtil {
    val rawDate: List<LocalDate>
        get() {
            val today = LocalDate.now()
            LocalDateTime.now().hour
            return generateSequence(today) { it.plusDays(1) }
                .take(91) // 오늘 포함 90일 후까지
                .toList()
        }

    val dates: List<String>
        get() = rawDate.map { it.getFormattedDateStr(WishBoardDateFormat.YY_M_D_KR) }

    val rawHour: List<Int>
        get() = (0..23).toList()

    val hours: List<String>
        get() = rawHour.map { it.getFormattedNumberPickerTime() }

    val rawMinute: List<Int>
        get() = listOf(0, 30)

    val minutes: List<String>
        get() = rawMinute.map { it.getFormattedNumberPickerTime() }

    fun kotlinx.datetime.LocalDate.getFormattedNumberPickerDate(): String =
        this.getFormattedDateStr(WishBoardDateFormat.YY_M_D_KR)

    fun Int.getFormattedNumberPickerTime(): String = this.toString().padStart(2, '0')

    fun toLocalDateTime(date: String, hour: String, minute: String): kotlinx.datetime.LocalDateTime? {
        if (date.isBlank() || hour.isBlank() || minute.isBlank()) return null
        val formatter = DateTimeFormatter.ofPattern(WishBoardDateFormat.YY_M_D_HH_MM_KR)
        val dateTimeString = "$date $hour:$minute"
        val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
        return localDateTime.toKotlinLocalDateTime()
    }
}
