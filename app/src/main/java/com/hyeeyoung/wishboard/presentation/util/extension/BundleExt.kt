package com.hyeeyoung.wishboard.presentation.util.extension

import android.os.Bundle
import timber.log.Timber
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

inline fun <reified T> Bundle.getJson(key: String): T? = getString(key)?.fromJson<T>()

@OptIn(ExperimentalEncodingApi::class)
inline fun <reified T> Bundle.getBase64Json(key: String): T? {
    return try {
        getString(key)?.let { base64String ->
            Base64.UrlSafe.decode((base64String as CharSequence)).toString(Charsets.UTF_8).fromJson<T>()
        }
    } catch (e: Exception) {
        Timber.e("Url EncodeJson Error : $e")
        null
    }
}
