package com.hyeeyoung.wishboard.presentation.model

/** 스타일, 하이퍼링크 등의 속성을 적용할 스트링  */
sealed class WishBoardString {
    data class NormalString(val value: String) : WishBoardString()

    data class SpanString(val value: String) : WishBoardString()

    data class LinkedString(val value: String, val tag: String, val link: String) : WishBoardString()
}
