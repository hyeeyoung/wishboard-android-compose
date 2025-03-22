package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.lifecycle.SavedStateHandle
import timber.log.Timber
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
inline fun <reified T> SavedStateHandle.getBase64Json(key: String): T? {
    return try {
        get<String>(key)?.let { base64String ->
            Base64.UrlSafe.decode((base64String as CharSequence)).toString(Charsets.UTF_8).fromJson<T>()
        }
    } catch (e: Exception) {
        Timber.e("Url EncodeJson Error : $e")
        null
    }
}
