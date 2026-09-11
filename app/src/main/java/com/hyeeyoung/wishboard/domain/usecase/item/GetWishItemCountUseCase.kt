package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.WishItemCount
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class GetWishItemCountUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(): Result<WishItemCount> =
        repository.getItemCount()
}
