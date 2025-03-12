package com.hyeeyoung.wishboard.presentation.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.format.DateTimeFormatter

object WishBoardDateFormat {
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"

    fun String.applyFormat(pattern: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val localDateTime = java.time.LocalDateTime.parse(this, formatter)
        return localDateTime.toKotlinLocalDateTime()
    }
}
