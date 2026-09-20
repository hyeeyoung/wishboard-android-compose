package com.hyeeyoung.wishboard.domain.util

/**
 * String에 대응되는 Enum 타입으로 변환하는 함수
 * */
inline fun <reified T : Enum<T>> safeValueOf(type: String?): T? {
    if (type == null) return null
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    }
}
