package com.hyeeyoung.wishboard.designsystem.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

class PriceVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val formattedNumber = digits.takeIf { it.isNotEmpty() }
            ?.let { DecimalFormat("#,###").format(it.toLong()) }
            ?: ""

        val transformedText = if (formattedNumber.isNotEmpty()) {
            "$KRW$formattedNumber"
        } else {
            ""
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (formattedNumber.isEmpty()) return 0

                val commaCount = (0 until offset.coerceAtMost(digits.length))
                    .count { i -> i > 0 && (digits.length - i) % 3 == 0 }
                val transformedOffset = offset + commaCount + KRW.length // ₩ 만큼 앞으로 보정

                // 끝까지 허용
                val maxOffset = transformedText.length

                return transformedOffset.coerceIn(KRW.length, maxOffset)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (formattedNumber.isEmpty()) return 0

                // ₩ 다음부터만 실제 숫자 영역
                val safeOffset = (offset - KRW.length).coerceAtLeast(0)
                    .coerceAtMost(formattedNumber.length)

                val commasBefore = (0 until safeOffset)
                    .count { i -> formattedNumber[i] == ',' }

                return (safeOffset - commasBefore).coerceAtLeast(0)
            }
        }

        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }

    companion object {
        private const val KRW = "₩ "
    }
}
