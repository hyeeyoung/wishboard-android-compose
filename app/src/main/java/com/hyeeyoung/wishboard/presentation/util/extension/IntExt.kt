package com.hyeeyoung.wishboard.presentation.util.extension

import java.text.DecimalFormat

fun Int?.setPriceFormat(): String {
    val decimalFormat = DecimalFormat("#,###")
    return decimalFormat.format(this ?: 0)
}
