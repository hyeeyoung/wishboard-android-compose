package com.hyeeyoung.wishboard.presentation.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishBoardPullToRefreshBox(
    modifier: Modifier = Modifier,
    loadState: LoadState,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    var isUserRefreshing by remember { mutableStateOf(false) }

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isUserRefreshing,
        onRefresh = {
            isUserRefreshing = true
            onRefresh()
        },
    ) {
        content()
    }

    LaunchedEffect(loadState) {
        if (loadState !is LoadState.Loading) {
            isUserRefreshing = false
        }
    }
}
