package com.hyeeyoung.wishboard.presentation.model

/** ClickableText 컴포저블에서 사용하기 위한 데이터 클래스 */
data class LinkedString(
    val str: String,
    val linkInfo: LinkInfo? = null,
) {
    data class LinkInfo(
        val tag: String,
        val link: String,
    )
}
