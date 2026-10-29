package com.hyeeyoung.wishboard.data.remote.parser

import java.net.URLDecoder

/**
 * 앱 실행용 링크(앱 브릿지)에서 실제 웹 URL을 뽑아낸다.
 * - 안드로이드 intent 링크: `intent://...#Intent;...;S.browser_fallback_url=<웹 URL>;end`
 * - 커스텀 스킴 링크: `naversearchapp://inappbrowser?url=<웹 URL>&...`
 *
 * WebView가 이런 링크로 이동하려 하면 외부 앱이 실행되거나 로딩이 멈추므로, 웹 URL을 찾아 그쪽으로 다시 로드한다.
 */
object AppBridgeUrlResolver {
    private val URL_PARAM_KEYS = listOf(
        "url",
        "link",
        "target_url",
        "targeturl",
        "redirect_url",
        "redirecturl",
        "fallback_url",
        "fallbackurl",
    )
    private const val INTENT_EXTRAS_DELIMITER = "#Intent;"
    private const val FALLBACK_EXTRA_KEY = "S.browser_fallback_url"

    fun isWebUrl(url: String): Boolean =
        url.startsWith("http://", ignoreCase = true) || url.startsWith("https://", ignoreCase = true)

    /** [rawUrl]이 앱 브릿지 링크이고 웹 URL을 찾았다면 그 URL을, 그 외에는 null을 반환한다. */
    fun resolve(rawUrl: String): String? {
        val scheme = rawUrl.substringBefore(':', missingDelimiterValue = "").lowercase()
        return when {
            scheme.isEmpty() || scheme == "http" || scheme == "https" -> null
            scheme == "intent" -> resolveIntent(rawUrl)
            else -> findWebUrlInQuery(rawUrl.substringBefore('#'))
        }
    }

    private fun resolveIntent(rawUrl: String): String? {
        val uriPart = rawUrl.substringBefore(INTENT_EXTRAS_DELIMITER)
        val extras = rawUrl.substringAfter(INTENT_EXTRAS_DELIMITER, missingDelimiterValue = "")
            .split(';')
            .mapNotNull { entry ->
                val key = entry.substringBefore('=', missingDelimiterValue = "")
                if (key.isEmpty()) null else key to entry.substringAfter('=')
            }
            .toMap()

        extras[FALLBACK_EXTRA_KEY]?.let(::decode)?.takeIf(::isWebUrl)?.let { return it }

        // intent://host/path?query#Intent;scheme=https;... → https://host/path?query
        val intentScheme = extras["scheme"]?.lowercase()
        if (intentScheme == "http" || intentScheme == "https") {
            return "$intentScheme:${uriPart.removePrefix("intent:")}"
        }
        return findWebUrlInQuery(uriPart)
    }

    private fun findWebUrlInQuery(uri: String): String? {
        val query = uri.substringAfter('?', missingDelimiterValue = "")
        if (query.isEmpty()) return null
        return query.split('&').firstNotNullOfOrNull { pair ->
            val key = pair.substringBefore('=').lowercase()
            if (key !in URL_PARAM_KEYS) return@firstNotNullOfOrNull null
            val value = decode(pair.substringAfter('=', missingDelimiterValue = "")) ?: return@firstNotNullOfOrNull null
            // 한 번 더 인코딩되어 있는 경우까지 대응
            value.takeIf(::isWebUrl) ?: decode(value)?.takeIf(::isWebUrl)
        }
    }

    private fun decode(value: String): String? =
        runCatching { URLDecoder.decode(value, "UTF-8") }.getOrNull()
}
