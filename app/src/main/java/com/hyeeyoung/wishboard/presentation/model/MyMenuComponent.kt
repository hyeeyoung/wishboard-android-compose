package com.hyeeyoung.wishboard.presentation.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

sealed class MyMenuComponent {
    data class Menu(
        @StringRes val nameRes: Int,
        val onClickMenu: (() -> Unit)? = null,
        val endComponent: (@Composable () -> Unit)? = null,
    ) : MyMenuComponent()

    object Divider : MyMenuComponent()
}
