package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class GetWishListUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(): Result<List<WishItem>> =
        repository.fetchWishList()
}
