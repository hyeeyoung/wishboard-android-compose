package com.hyeeyoung.wishboard.util.extension

import java.text.DecimalFormat

fun Int?.setPriceFormat(): String {
    val decimalFormat = DecimalFormat("#,###")
    return decimalFormat.format(this ?: 0)
}
