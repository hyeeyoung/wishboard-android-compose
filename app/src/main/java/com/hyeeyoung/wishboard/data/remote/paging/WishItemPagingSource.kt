package com.hyeeyoung.wishboard.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hyeeyoung.wishboard.data.remote.service.ItemService
import com.hyeeyoung.wishboard.presentation.upload.model.WishItemDto

class WishItemPagingSource(
    private val itemService: ItemService,
) : PagingSource<Int, WishItemDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WishItemDto> {
        val currentPage = params.key ?: 0

        return try {
            val response = itemService.fetchWishList(
                page = currentPage,
                size = params.loadSize,
            )

            val items = response.data.content
            LoadResult.Page(
                data = items,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (response.data.last) null else currentPage + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, WishItemDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
