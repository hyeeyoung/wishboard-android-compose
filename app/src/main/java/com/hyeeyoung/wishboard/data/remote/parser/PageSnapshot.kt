package com.hyeeyoung.wishboard.data.remote.parser

import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/** WebView에서 실행한 자바스크립트가 현재 DOM에서 뽑아 온 값. 해석은 [PageSnapshotMapper]가 한다. */
@Serializable
data class PageSnapshot(
    val url: String? = null,
    val title: String? = null,
    val ogTitle: String? = null,
    /** 절대경로로 변환된 og:image */
    val ogImage: String? = null,
    /** [PageSnapshotMapper.PRICE_META_KEYS] 우선순위대로, 값이 있는 메타 태그의 content */
    val priceMeta: List<String> = emptyList(),
    /** `<script type="application/ld+json">`의 원문 */
    val jsonLd: List<String> = emptyList(),
    /** 클래스명에 price가 들어간 요소의 텍스트 (최후의 수단) */
    val priceTexts: List<String> = emptyList(),
) {
    val isEmpty: Boolean
        get() = title.isNullOrBlank() && ogTitle.isNullOrBlank() && ogImage.isNullOrBlank() &&
            priceMeta.isEmpty() && jsonLd.isEmpty() && priceTexts.isEmpty()
}

object PageSnapshotMapper {
    /** 자바스크립트에서 값을 뽑을 때 쓰는 가격 메타 태그 키 (우선순위 순) */
    val PRICE_META_KEYS = listOf(
        "og:price:amount",
        "product:price:amount",
        "product:sale_price:amount",
        "eg:salePrice",
        "eg:originalPrice",
    )

    private val OFFER_PRICE_KEYS = listOf("price", "lowPrice")
    private val NUMBER_REGEX = Regex("""\d[\d,]*(\.\d+)?""")
    private val WON_PRICE_REGEX = Regex("""(\d[\d,]*)\s*원""")
    private val json = Json { ignoreUnknownKeys = true }

    /** 이름/이미지/가격을 모두 찾지 못하면 null */
    fun toParsedItem(snapshot: PageSnapshot): ParsedWishItem? {
        val name = findName(snapshot)
        val image = snapshot.ogImage?.trim()?.ifEmpty { null }
        val price = findPrice(snapshot)
        if (name == null && image == null && price == null) return null
        return ParsedWishItem(image = image, name = name, price = price)
    }

    /** 이름(og:title), 이미지, 가격이 모두 채워져 더 기다릴 필요가 없는 상태인지 */
    fun isComplete(snapshot: PageSnapshot): Boolean =
        !snapshot.ogTitle.isNullOrBlank() && !snapshot.ogImage.isNullOrBlank() && findPrice(snapshot) != null

    /** og:title, 없으면 document.title */
    fun findName(snapshot: PageSnapshot): String? =
        snapshot.ogTitle?.trim()?.ifEmpty { null } ?: snapshot.title?.trim()?.ifEmpty { null }

    /** 가격 메타 태그 → JSON-LD offers.price → price 클래스 요소 텍스트 순 */
    fun findPrice(snapshot: PageSnapshot): String? =
        snapshot.priceMeta.firstNotNullOfOrNull(::normalizePrice)
            ?: snapshot.jsonLd.firstNotNullOfOrNull(::findJsonLdPrice)
            ?: snapshot.priceTexts.firstNotNullOfOrNull { text ->
                normalizePrice(WON_PRICE_REGEX.find(text)?.groupValues?.get(1) ?: text)
            }

    /** "73,900원", "73900.00", "KRW 73900" 같은 값에서 양의 정수 가격 문자열(예: "73900")을 만든다. */
    fun normalizePrice(raw: String): String? {
        val number = NUMBER_REGEX.find(raw)?.value?.replace(",", "") ?: return null
        val price = number.toBigDecimalOrNull()?.toBigInteger() ?: return null
        return if (price.signum() > 0) price.toString() else null
    }

    private fun findJsonLdPrice(raw: String): String? {
        val root = runCatching { json.parseToJsonElement(raw) }.getOrNull() ?: return null
        return findOfferPrice(root)
    }

    private fun findOfferPrice(element: JsonElement): String? = when (element) {
        is JsonArray -> element.firstNotNullOfOrNull(::findOfferPrice)
        is JsonObject -> element["offers"]?.let(::findPriceInOffers)
            ?: element["@graph"]?.let(::findOfferPrice)
        else -> null
    }

    private fun findPriceInOffers(offers: JsonElement): String? = when (offers) {
        is JsonArray -> offers.firstNotNullOfOrNull(::findPriceInOffers)
        is JsonObject -> OFFER_PRICE_KEYS.firstNotNullOfOrNull { key ->
            (offers[key] as? JsonPrimitive)?.contentOrNull?.let(::normalizePrice)
        } ?: offers["priceSpecification"]?.let(::findPriceInOffers)
        else -> null
    }
}
