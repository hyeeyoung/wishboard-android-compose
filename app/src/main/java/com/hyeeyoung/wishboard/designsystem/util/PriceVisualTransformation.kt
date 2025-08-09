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
            "$formattedNumber$KRW"
        } else {
            ""
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (formattedNumber.isEmpty()) return 0

                val commaCount = (0 until offset.coerceAtMost(digits.length))
                    .count { i -> i > 0 && (digits.length - i) % 3 == 0 }
                val transformedOffset = offset + commaCount

                // "원" 앞까지만 허용
                val maxOffset = (transformedText.length - KRW.length)
                    .coerceAtLeast(0)

                return transformedOffset.coerceIn(0, maxOffset)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (formattedNumber.isEmpty()) return 0

                val numberPart = formattedNumber
                val maxOffset = numberPart.length
                val safeOffset = offset.coerceIn(0, maxOffset)

                val commasBefore = (0 until safeOffset)
                    .count { i -> numberPart[i] == ',' }
                return (safeOffset - commasBefore).coerceAtLeast(0)
            }
        }

        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }

    companion object {
        private const val KRW = "원"
    }
}
