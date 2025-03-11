package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.WishItem
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class GetWishItemUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(): Result<List<WishItem>> =
        repository.fetchWishList()
}
