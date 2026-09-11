package com.hyeeyoung.wishboard.data.util.extension

import com.hyeeyoung.wishboard.presentation.util.TimeUtil
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

fun String?.toPlainRequestBody(): RequestBody =
    requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())

fun String?.toPlainNullableRequestBody(): RequestBody? =
    this?.toRequestBody("text/plain".toMediaTypeOrNull())

fun String.toInstantToLocalDateTime(): LocalDateTime = try {
    val localDateTime = LocalDateTime.parse(this)
    val instant = localDateTime.toInstant(TimeUtil.koreaTimeZone)
    instant.toLocalDateTime(TimeZone.currentSystemDefault())
} catch (e: Exception) {
    Timber.e("$e")
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}
