package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class GetParsedItemInfoUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(site: String): Result<ParsedWishItem> =
        repository.getParsedItemInfo(site)
}
