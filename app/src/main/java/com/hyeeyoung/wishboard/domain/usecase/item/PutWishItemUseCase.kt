package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class PutWishItemUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(itemId: Long, itemInfo: WishItemUploadInfo): Result<Unit> =
        repository.updateWishItem(itemId = itemId, itemInfo = itemInfo)
}
