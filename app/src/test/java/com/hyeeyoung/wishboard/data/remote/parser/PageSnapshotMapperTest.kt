package com.hyeeyoung.wishboard.data.remote.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PageSnapshotMapperTest {
    private fun parse(snapshot: PageSnapshot) = PageSnapshotMapper.toParsedItem(snapshot)

    @Test
    fun `og 값에서 이름, 이미지, 가격을 만든다`() {
        val item = parse(
            PageSnapshot(
                title = "document title",
                ogTitle = "오버핏 셔츠",
                ogImage = "https://shop.example.com/images/shirt.jpg",
                priceMeta = listOf("73,900"),
            ),
        )

        assertEquals("오버핏 셔츠", item?.name)
        assertEquals("https://shop.example.com/images/shirt.jpg", item?.image)
        assertEquals("73900", item?.price)
    }

    @Test
    fun `og title이 없으면 document title을 이름으로 사용한다`() {
        assertEquals("셔츠", parse(PageSnapshot(title = " 셔츠 "))?.name)
    }

    @Test
    fun `소수점 가격은 정수로 변환한다`() {
        assertEquals("73900", parse(PageSnapshot(priceMeta = listOf("73900.00")))?.price)
    }

    @Test
    fun `통화 문자가 섞여도 숫자를 뽑는다`() {
        assertEquals("73900", parse(PageSnapshot(priceMeta = listOf("KRW 73900")))?.price)
    }

    @Test
    fun `0원이나 숫자가 없는 값은 건너뛰고 다음 후보를 쓴다`() {
        assertEquals("5000", parse(PageSnapshot(priceMeta = listOf("0", "free", "5000")))?.price)
    }

    @Test
    fun `메타 태그에 가격이 없으면 JSON-LD offers price를 사용한다`() {
        val item = parse(
            PageSnapshot(
                jsonLd = listOf(
                    """{"@type":"Product","offers":{"@type":"Offer","price":"59000","priceCurrency":"KRW"}}""",
                ),
            ),
        )

        assertEquals("59000", item?.price)
    }

    @Test
    fun `JSON-LD가 graph와 offers 배열로 감싸져 있어도 가격을 찾는다`() {
        val item = parse(
            PageSnapshot(
                jsonLd = listOf(
                    """{"@graph":[{"@type":"WebSite"},
                        {"@type":"Product","offers":[{"@type":"Offer","price":12900}]}]}""",
                ),
            ),
        )

        assertEquals("12900", item?.price)
    }

    @Test
    fun `JSON-LD의 lowPrice와 priceSpecification도 읽는다`() {
        assertEquals(
            "8000",
            parse(PageSnapshot(jsonLd = listOf("""{"offers":{"lowPrice":"8000"}}""")))?.price,
        )
        assertEquals(
            "9000",
            parse(PageSnapshot(jsonLd = listOf("""{"offers":{"priceSpecification":{"price":"9000"}}}""")))?.price,
        )
    }

    @Test
    fun `잘못된 JSON-LD는 무시하고 다음 후보로 넘어간다`() {
        val item = parse(
            PageSnapshot(
                ogTitle = "셔츠",
                jsonLd = listOf("{ not json", """{"offers":{"price":"1000"}}"""),
            ),
        )

        assertEquals("1000", item?.price)
    }

    @Test
    fun `price 클래스 요소 텍스트는 원화 가격을 우선해서 추정한다`() {
        val item = parse(PageSnapshot(priceTexts = listOf("10% 73,900원")))

        assertEquals("73900", item?.price)
    }

    @Test
    fun `메타 태그가 JSON-LD보다 우선한다`() {
        val item = parse(
            PageSnapshot(
                priceMeta = listOf("1000"),
                jsonLd = listOf("""{"offers":{"price":"2000"}}"""),
            ),
        )

        assertEquals("1000", item?.price)
    }

    @Test
    fun `아무 정보도 없으면 null을 반환한다`() {
        assertNull(parse(PageSnapshot()))
        assertTrue(PageSnapshot().isEmpty)
    }

    @Test
    fun `이름, 이미지, 가격이 모두 있어야 완료로 본다`() {
        val partial = PageSnapshot(ogTitle = "셔츠", ogImage = "https://a.com/1.jpg")
        assertFalse(PageSnapshotMapper.isComplete(partial))
        assertTrue(PageSnapshotMapper.isComplete(partial.copy(priceMeta = listOf("1000"))))
    }
}
