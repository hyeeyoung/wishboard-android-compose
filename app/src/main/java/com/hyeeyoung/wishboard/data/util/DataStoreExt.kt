package com.hyeeyoung.wishboard.data.util

import android.content.SharedPreferences
import com.hyeeyoung.wishboard.presentation.util.extension.fromJson
import timber.log.Timber
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
inline fun <reified T> SharedPreferences.getBase64Json(key: String): T? {
    return try {
        getString(key, null)?.let { base64String ->
            Base64.UrlSafe.decode((base64String as CharSequence)).toString(Charsets.UTF_8).fromJson<T>()
        }
    } catch (e: Exception) {
        Timber.e("Url EncodeJson Error : $e")
        null
    }
}
