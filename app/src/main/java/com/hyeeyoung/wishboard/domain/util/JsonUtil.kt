package com.hyeeyoung.wishboard.domain.util

import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

object JsonUtil {
    val json = Json {
        isLenient = true
        prettyPrint = true
        explicitNulls = false
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            contextual(InstantIso8601Serializer)
            contextual(LocalDateTimeIso8601Serializer)
        }
    }
}
