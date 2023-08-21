package com.hyeeyoung.wishboard.presentation.model

/** wishBoardTextWithSpans 컴포저블에서 사용하기 위한 데이터 클래스 */
sealed class SpanString {
    data class Span(val value: String) : SpanString()
    data class NormalString(val value: String) : SpanString()
}
