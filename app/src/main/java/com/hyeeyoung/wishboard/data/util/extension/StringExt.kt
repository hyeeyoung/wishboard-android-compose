package com.hyeeyoung.wishboard.data.util.extension

import com.hyeeyoung.wishboard.presentation.util.TimeUtil.koreaTimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun String?.toPlainRequestBody(): RequestBody =
    requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())

fun String?.toPlainNullableRequestBody(): RequestBody? =
    this?.toRequestBody("text/plain".toMediaTypeOrNull())

fun String.toInstantToLocalDateTime(): LocalDateTime = try {
    Instant.parse(this).toLocalDateTime(timeZone = koreaTimeZone)
} catch (e: Exception) {
    Clock.System.now().toLocalDateTime(koreaTimeZone)
}

fun String.toInstantToLocalDate(): LocalDate = try {
    Instant.parse(this).toLocalDateTime(koreaTimeZone).date
} catch (e: Exception) {
    Clock.System.now().toLocalDateTime(koreaTimeZone).date
}
