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

/** 최초 진입 시의 로딩 상태(refresh = Loading)를 미리보기 위한 fake paging data */
@Composable
fun <T : Any> getFakeLoadingPagingData(): LazyPagingItems<T> {
    return MutableStateFlow(
        PagingData.empty<T>(
            sourceLoadStates = LoadStates(
                refresh = LoadState.Loading,
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
            ),
        ),
    ).collectAsLazyPagingItems()
}
