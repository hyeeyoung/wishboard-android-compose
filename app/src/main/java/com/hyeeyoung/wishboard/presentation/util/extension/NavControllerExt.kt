package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.annotation.MainThread
import androidx.navigation.NavController

@MainThread
fun NavController.safePopBackStack() {
    if (previousBackStackEntry != null) {
        popBackStack()
    }
}

fun <T> NavController.safePopBackStack(key: String, value: T) {
    this.previousBackStackEntry?.savedStateHandle?.set(key, value)
    this.safePopBackStack()
}
