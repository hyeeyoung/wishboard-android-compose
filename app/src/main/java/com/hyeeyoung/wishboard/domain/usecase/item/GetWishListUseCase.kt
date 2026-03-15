package com.hyeeyoung.wishboard.domain.usecase.item

import androidx.paging.PagingData
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWishListUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    operator fun invoke(): Flow<PagingData<WishItem>> =
        repository.fetchWishList()
}
