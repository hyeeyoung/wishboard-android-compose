package com.hyeeyoung.wishboard.domain.repository

import androidx.paging.PagingData
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun fetchWishList(): Flow<PagingData<WishItem>>

    suspend fun fetchWishItemDetail(itemId: Long): Result<WishItemDetail>

    suspend fun uploadWishItem(uploadType: WishItemUploadType, itemInfo: WishItemUploadInfo): Result<Long>

    suspend fun updateWishItem(itemId: Long, itemInfo: WishItemUploadInfo): Result<Unit>

    suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Result<Unit>

    suspend fun deleteWishItem(itemId: Long): Result<Unit>

    suspend fun getParsedItemInfo(site: String): Result<ParsedWishItem?>

    suspend fun updateItemOwnership(itemId: Long, isOwnedItem: Boolean): Result<Boolean>
}
