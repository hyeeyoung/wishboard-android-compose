package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail

interface ItemRepository {
    suspend fun fetchWishList(): Result<List<WishItem>>
    suspend fun fetchWishItemDetail(itemId: Long): Result<List<WishItemDetail>>

//    suspend fun uploadWishItem(
//        folderId: RequestBody?,
//        itemName: RequestBody,
//        itemPrice: RequestBody?,
//        itemUrl: RequestBody?,
//        itemNotificationType: RequestBody?,
//        itemNotificationDate: RequestBody?,
//        image: MultipartBody.Part?,
//        itemMemo: RequestBody? = null,
//    ): Result<Boolean>
//
//    suspend fun updateWishItem(
//        itemId: Long,
//        folderId: RequestBody?,
//        itemName: RequestBody,
//        itemPrice: RequestBody?,
//        itemMemo: RequestBody?,
//        itemUrl: RequestBody?,
//        itemNotificationType: RequestBody?,
//        itemNotificationDate: RequestBody?,
//        itemImage: MultipartBody.Part?
//    ): Result<Pair<Boolean, Int>?>
//
    suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Result<Unit>

//    suspend fun deleteWishItem(itemId: Long): Result<Boolean>
//    suspend fun getItemParsingInfo(site: String): Result<Pair<ItemInfo?, Int>?>
}
