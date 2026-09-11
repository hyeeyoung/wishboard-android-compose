package com.hyeeyoung.wishboard.presentation.util.extension

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController

@MainThread
fun NavController.safePopBackStack() {
    if (previousBackStackEntry != null) {
        popBackStack()
    }
}

fun NavController.navigateIfResumed(
    lifecycleOwner: LifecycleOwner,
    route: String,
) {
    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
        this.navigate(route)
    }
}

fun <T> NavController.safePopBackStack(key: String, value: T) {
    this.previousBackStackEntry?.savedStateHandle?.set(key, value)
    this.safePopBackStack()
}
