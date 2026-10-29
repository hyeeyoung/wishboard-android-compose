package com.hyeeyoung.wishboard.data.remote.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppBridgeUrlResolverTest {
    @Test
    fun `커스텀 스킴의 url 쿼리 파라미터에서 웹 URL을 뽑는다`() {
        val resolved = AppBridgeUrlResolver.resolve(
            "naversearchapp://inappbrowser?" +
                "url=https%3A%2F%2Fm.shopping.naver.com%2Fproducts%2F123%3Fa%3D1&target=new&version=6",
        )

        assertEquals("https://m.shopping.naver.com/products/123?a=1", resolved)
    }

    @Test
    fun `이중 인코딩된 url도 풀어낸다`() {
        val resolved = AppBridgeUrlResolver.resolve(
            "naversearchapp://inappbrowser?url=https%253A%252F%252Fshop.example.com%252Fp%252F1",
        )

        assertEquals("https://shop.example.com/p/1", resolved)
    }

    @Test
    fun `intent 링크의 browser_fallback_url을 우선해서 사용한다`() {
        val resolved = AppBridgeUrlResolver.resolve(
            "intent://product/1#Intent;scheme=shopapp;package=com.example.shop;" +
                "S.browser_fallback_url=https%3A%2F%2Fm.example.com%2Fproduct%2F1;end",
        )

        assertEquals("https://m.example.com/product/1", resolved)
    }

    @Test
    fun `fallback이 없는 https intent 링크는 웹 URL로 되돌린다`() {
        val resolved = AppBridgeUrlResolver.resolve(
            "intent://m.example.com/product/1?x=1#Intent;scheme=https;package=com.example.shop;end",
        )

        assertEquals("https://m.example.com/product/1?x=1", resolved)
    }

    @Test
    fun `웹 URL을 찾을 수 없는 앱 링크는 null이다`() {
        assertNull(AppBridgeUrlResolver.resolve("shopapp://product/1"))
        assertNull(AppBridgeUrlResolver.resolve("intent://product/1#Intent;scheme=shopapp;end"))
        assertNull(AppBridgeUrlResolver.resolve("myapp://x?url=javascript%3Aalert(1)"))
    }

    @Test
    fun `일반 웹 URL은 브릿지가 아니므로 null이다`() {
        assertNull(AppBridgeUrlResolver.resolve("https://m.example.com/product/1?url=https%3A%2F%2Fa.com"))
        assertTrue(AppBridgeUrlResolver.isWebUrl("HTTPS://a.com"))
        assertFalse(AppBridgeUrlResolver.isWebUrl("intent://a"))
    }
}
