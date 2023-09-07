package com.hyeeyoung.wishboard.presentation.util.extension

import java.net.URL
import java.text.NumberFormat
import java.util.Locale

/** url에서 도메인명 추출 */
fun String.getDomainName(): String? {
    val host = URL(this).host
    return if (host.startsWith("www.")) host.substring(4) else host
}

/** 유효한 가격 문자열로 만들기 위해 적절하지 않는 문자를 제거 (ex. "123abc" -> "123", "000" -> "0") */
fun String.makeValidPriceStr() = this.filter { it.isDigit() }.toIntOrNull()?.toString()

private const val EMPTY = -1

/** 유효한 가격 문자열에 대해 천 단위 구분자로 컴마 찍기 */
fun String.applyPriceFormat(): String {
    val price = this.toIntOrNull()
    val formattedStr = NumberFormat.getNumberInstance(Locale.US).format(price ?: EMPTY)
    return if (formattedStr != EMPTY.toString()) formattedStr else ""
}
