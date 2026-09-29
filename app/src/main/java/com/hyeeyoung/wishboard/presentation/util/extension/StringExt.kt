package com.hyeeyoung.wishboard.presentation.util.extension

import android.util.Patterns
import android.webkit.URLUtil
import java.net.MalformedURLException
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

fun String.isEmptyOrBlank() = this.isEmpty() || this.isBlank()

/** 스킴(http/https)이 없으면 https를 붙여 반환한다. (ex. "ver.me/xxx" -> "https://ver.me/xxx") */
fun String.withScheme(): String =
    if (startsWith("http://") || startsWith("https://")) this else "https://$this"

/**
 * url에서 도메인명 추출. Patterns.WEB_URL로 매칭된 링크는 스킴이 없을 수 있어(ex. "ver.me/xxx"),
 * java.net.URL이 요구하는 스킴을 보정한 뒤 파싱하고, 그래도 유효하지 않으면 null을 반환한다.
 */
fun String.getDomainName(): String? {
    return try {
        val host = URL(this.withScheme()).host
        if (host.startsWith("www.")) host.substring(4) else host
    } catch (e: MalformedURLException) {
        null
    }
}

/** 유효한 가격 문자열로 만들기 위해 적절하지 않는 문자를 제거 (ex. "123abc" -> "123", "000" -> "0") */
fun String.makeValidPriceStr(): String? {
    val input = if (this.length > 9) this.take(9) else this
    return input.filter { it.isDigit() }.toIntOrNull()?.toString()
}

private const val EMPTY = -1

/** 유효한 가격 문자열에 대해 천 단위 구분자로 컴마 찍기 */
fun String.applyPriceFormat(): String {
    val price = this.toIntOrNull()
    val formattedStr = NumberFormat.getNumberInstance(Locale.US).format(price ?: EMPTY)
    return if (formattedStr != EMPTY.toString()) formattedStr else ""
}

fun String.getValidUrl(): String? {
    return if (this.checkValidationItemUrl()) {
        this
    } else {
        this.extractLinks()
    }
}

/** url 유효성 검증 */
fun String.checkValidationItemUrl(): Boolean {
    if (this.isBlank()) {
        return false
    }

    return URLUtil.isValidUrl(this) && Patterns.WEB_URL.matcher(this).matches()
}

private fun getRefinedUrl(url: String): String? {
    val httpStartIdx = url.indexOf("http")
    if (httpStartIdx == -1) return null
    val refinedUrl = url.substring(httpStartIdx)

    var httpEndIdx = refinedUrl.indexOf(" ")
    if (httpEndIdx == -1) {
        httpEndIdx = refinedUrl.indexOf("\n")
    }

    return if (httpEndIdx == -1) {
        refinedUrl
    } else {
        refinedUrl.substring(0, httpEndIdx)
    }
}

/** 쿠팡 > 앱 내 공유하기 버튼 클릭 > 위시보드로 공유할 경우 url 앞에 한글이 붙기 때문에 유효한 url만 떼어내고자 해당 함수에서 url을 가공함 */
fun String.extractLinks(): String? {
    val regex = """(https?://\S+)""".toRegex()
    return regex.find(this)?.value
}
