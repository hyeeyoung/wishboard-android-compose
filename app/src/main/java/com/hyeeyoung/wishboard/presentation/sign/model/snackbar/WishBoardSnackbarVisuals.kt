package com.hyeeyoung.wishboard.presentation.sign.model.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class WishBoardSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val vibrate: Boolean = false,
) : SnackbarVisuals
