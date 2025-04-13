package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class DeleteWishItemUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(itemId: Long): Result<Unit> =
        repository.deleteWishItem(itemId = itemId)
}
