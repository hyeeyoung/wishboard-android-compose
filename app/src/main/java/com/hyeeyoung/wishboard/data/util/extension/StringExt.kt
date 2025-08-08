package com.hyeeyoung.wishboard.data.util.extension

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
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
    val fixedString = if (!this.endsWith("Z") && !this.contains("+")) {
        this + "Z"
    } else {
        this
    }

    Instant.parse(fixedString).toLocalDateTime(TimeZone.currentSystemDefault())
} catch (e: Exception) {
    Timber.e("$e")
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}
