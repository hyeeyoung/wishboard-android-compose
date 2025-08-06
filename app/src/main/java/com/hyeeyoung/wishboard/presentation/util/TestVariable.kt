package com.hyeeyoung.wishboard.presentation.util

import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun <T : Any> getFakePagingData(data: List<T>): LazyPagingItems<T> {
    return MutableStateFlow(
        PagingData.from(
            data = data,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(true),
                prepend = LoadState.NotLoading(false),
            ),
            mediatorLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(true),
                prepend = LoadState.NotLoading(false),
            ),
        ),
    ).collectAsLazyPagingItems()
}
