package com.hyeeyoung.wishboard.presentation.util.extension

import java.text.NumberFormat
import java.util.Locale

fun Int?.setPriceFormat(): String =
    NumberFormat.getNumberInstance(Locale.US).format(this ?: 0)
