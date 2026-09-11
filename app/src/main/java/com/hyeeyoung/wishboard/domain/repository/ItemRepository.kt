package com.hyeeyoung.wishboard.domain.repository

import androidx.paging.PagingData
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemCount
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ItemRepository {
    val totalElements: StateFlow<Int?>
    fun fetchWishList(itemStatus: WishItemOwnershipStatus? = null): Flow<PagingData<WishItem>>

    suspend fun fetchWishItemDetail(itemId: Long): Result<WishItemDetail>

    suspend fun uploadWishItem(uploadType: WishItemUploadType, itemInfo: WishItemUploadInfo): Result<Long>

    suspend fun updateWishItem(itemId: Long, itemInfo: WishItemUploadInfo): Result<Unit>

    suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Result<Unit>

    suspend fun deleteWishItem(itemId: Long): Result<Unit>

    suspend fun getParsedItemInfo(site: String): Result<ParsedWishItem?>

    suspend fun updateItemOwnership(itemId: Long, isOwnedItem: Boolean): Result<Boolean>

    suspend fun getItemCount(): Result<WishItemCount>
}
