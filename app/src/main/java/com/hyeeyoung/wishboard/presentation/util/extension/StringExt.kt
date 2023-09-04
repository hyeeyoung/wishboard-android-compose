package com.hyeeyoung.wishboard.presentation.util.extension

import java.net.URL

/** url에서 도메인명 추출 */
fun String.getDomainName(): String? {
    val host = URL(this).host
    return if (host.startsWith("www.")) host.substring(4) else host
}
