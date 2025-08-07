package com.hyeeyoung.wishboard.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.remote.model.base.PagedResponse

class GeneralPagingSource<T : Any>(
    private val loadPage: suspend (page: Int, size: Int) -> BaseResponse<PagedResponse<T>>,
) : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val currentPage = params.key ?: 0

        return try {
            val res = loadPage(currentPage, params.loadSize)

            val items = res.data.content
            LoadResult.Page(
                data = items,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (res.data.last) null else currentPage + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
