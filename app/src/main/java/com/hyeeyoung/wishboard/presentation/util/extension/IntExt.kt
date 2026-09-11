package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import java.text.NumberFormat
import java.util.Locale

fun Int?.setPriceFormat(): String =
    NumberFormat.getNumberInstance(Locale.US).format(this ?: 0)

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }
