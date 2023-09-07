package com.hyeeyoung.wishboard.designsystem.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.hyeeyoung.wishboard.presentation.util.extension.applyPriceFormat

class PriceTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedString = text.text.applyPriceFormat().run { if (isNotEmpty()) "₩ $this" else this }
        return TransformedText(
            text = AnnotatedString(formattedString),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int = formattedString.length
                override fun transformedToOriginal(offset: Int): Int = text.length
            },
        )
    }
}
