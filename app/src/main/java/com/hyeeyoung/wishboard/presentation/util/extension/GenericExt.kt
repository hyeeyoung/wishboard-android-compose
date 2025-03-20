package com.hyeeyoung.wishboard.presentation.util.extension

import com.hyeeyoung.wishboard.domain.util.JsonUtil
import kotlinx.serialization.encodeToString
import timber.log.Timber
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

inline fun <reified T> T.toJson(): String {
    return JsonUtil.json.encodeToString(this)
}

inline fun <reified T> String.fromJson(): T {
    return JsonUtil.json.decodeFromString(this)
}

@OptIn(ExperimentalEncodingApi::class)
inline fun <reified T> T?.toBase64Json(): String? {
    return this?.let {
        try {
            Base64.UrlSafe.encode(JsonUtil.json.encodeToString(this).encodeToByteArray())
        } catch (e: Exception) {
            Timber.e("Url EncodeJson Error : $e")
            null
        }
    }
}

