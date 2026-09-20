package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class GetTotalWishItemsUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    operator fun invoke() = repository.totalElements
}
