package com.hyeeyoung.wishboard.presentation.util

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber

object WishBoardEventBus {
    private val _onWishItemChanged = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val _onFolderChanged = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val onWishItemChanged: SharedFlow<Unit> = _onWishItemChanged.asSharedFlow()
    val onFolderChanged: SharedFlow<Unit> = _onFolderChanged.asSharedFlow()

    fun notifyWishItemChanged() {
        Timber.e("WishBoardEventBus notifyWishItemChanged")
        _onWishItemChanged.tryEmit(Unit)
    }
    fun notifyFolderChanged() {
        _onFolderChanged.tryEmit(Unit)
    }
}
