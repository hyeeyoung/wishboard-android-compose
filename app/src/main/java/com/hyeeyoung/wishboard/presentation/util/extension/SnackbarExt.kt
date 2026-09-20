package com.hyeeyoung.wishboard.presentation.util.extension

/** 스낵바 관련 학장 함수를 모아놓은 파일 */
internal fun androidx.compose.material3.SnackbarDuration.toMillis(): Long =
    when (this) {
        androidx.compose.material3.SnackbarDuration.Short -> 2000L
        androidx.compose.material3.SnackbarDuration.Long -> 4000L
        androidx.compose.material3.SnackbarDuration.Indefinite -> Long.MAX_VALUE
    }
