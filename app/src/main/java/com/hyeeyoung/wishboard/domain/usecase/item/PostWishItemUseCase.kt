package com.hyeeyoung.wishboard.domain.usecase.item

import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class PostWishItemUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(uploadType: WishItemUploadType, itemInfo: WishItemUploadInfo): Result<Unit> =
        repository.uploadWishItem(uploadType = uploadType, itemInfo = itemInfo)
}
