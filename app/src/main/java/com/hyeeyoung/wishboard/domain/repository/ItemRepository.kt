package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType

interface ItemRepository {
    suspend fun fetchWishList(): Result<List<WishItem>>

    suspend fun fetchWishItemDetail(itemId: Long): Result<List<WishItemDetail>>

    suspend fun uploadWishItem(uploadType: WishItemUploadType, itemInfo: WishItemUploadInfo): Result<Long>

    suspend fun updateWishItem(itemId: Long, itemInfo: WishItemUploadInfo): Result<Unit>

    suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Result<Unit>

    suspend fun deleteWishItem(itemId: Long): Result<Unit>

    suspend fun getParsedItemInfo(site: String): Result<ParsedWishItem>
}
