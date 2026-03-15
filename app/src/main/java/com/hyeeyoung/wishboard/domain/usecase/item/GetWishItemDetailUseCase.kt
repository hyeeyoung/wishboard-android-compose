package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class GetWishItemDetailUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(id: Long): Result<WishItemDetail> =
        repository.fetchWishItemDetail(id)
}
