package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.domain.model.WishItem

interface ItemRepository {
    suspend fun fetchWishList(): Result<List<WishItem>>
//    suspend fun fetchLatestWishItem(): WishItem?
//    suspend fun fetchWishItemDetail(itemId: Long): List<ItemDetail>?
//    suspend fun uploadWishItem(
//        folderId: RequestBody?,
//        itemName: RequestBody,
//        itemPrice: RequestBody?,
//        itemUrl: RequestBody?,
//        itemNotificationType: RequestBody?,
//        itemNotificationDate: RequestBody?,
//        image: MultipartBody.Part?,
//        itemMemo: RequestBody? = null,
//    ): Boolean
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
//    ): Pair<Boolean, Int>?
//
//    suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Boolean
//    suspend fun deleteWishItem(itemId: Long): Boolean
//    suspend fun getItemParsingInfo(site: String): Pair<ItemInfo?, Int>?
}
