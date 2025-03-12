package com.hyeeyoung.wishboard.data.remote.repository

import com.hyeeyoung.wishboard.data.remote.service.ItemService
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemService: ItemService,
) : ItemRepository {
    override suspend fun fetchWishList(): Result<List<WishItem>> = runCatching {
        itemService.fetchWishList().map { it.toDomain() }
    }

    override suspend fun fetchWishItemDetail(itemId: Long): Result<List<WishItemDetail>> =
        runCatching {
            itemService.fetchWishItemDetail(itemId).map { it.toDomain() }
        }
//
//    override suspend fun uploadWishItem(
//        folderId: RequestBody?,
//        itemName: RequestBody,
//        itemPrice: RequestBody?,
//        itemUrl: RequestBody?,
//        itemNotificationType: RequestBody?,
//        itemNotificationDate: RequestBody?,
//        image: MultipartBody.Part?,
//        itemMemo: RequestBody?,
//    ): Result<Boolean> = runCatching {
//        wishItemService.uploadWishItem(
//            folderId,
//            itemName,
//            itemPrice,
//            itemMemo,
//            itemUrl,
//            itemNotificationType,
//            itemNotificationDate,
//            image
//        )
//    }.fold({
//        Timber.d("아이템 등록 성공(${it.code()})")
//        it.isSuccessful
//    }, {
//        Timber.e("아이템 등록 실패(${it.message})")
//        false
//    })
//
//    override suspend fun updateWishItem(
//        itemId: Long,
//        folderId: RequestBody?,
//        itemName: RequestBody,
//        itemPrice: RequestBody?,
//        itemMemo: RequestBody?,
//        itemUrl: RequestBody?,
//        itemNotificationType: RequestBody?,
//        itemNotificationDate: RequestBody?,
//        itemImage: MultipartBody.Part?
//    ): Result<Pair<Boolean, Int>?> = runCatching {
//        wishItemService.updateToWishItem(
//            itemId, folderId,
//            itemName,
//            itemPrice,
//            itemMemo,
//            itemUrl,
//            itemNotificationType,
//            itemNotificationDate,
//            itemImage
//        )
//    }.fold({
//        Timber.d("아이템 수정 성공(${it.code()})")
//        Pair(it.isSuccessful, it.code())
//    }, {
//        Timber.e("아이템 수정 실패: ${it.message}")
//        null
//    })
//
//    override suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Result<Boolean> =
//        runCatching {
//            wishItemService.updateFolderOfItem(itemId, folderId)
//        }.fold({
//            Timber.d("아이템 폴더 수정 성공(${it.code()})")
//            it.isSuccessful
//        }, {
//            Timber.e("아이템 폴더 수정 실패: ${it.message}")
//            false
//        })
//
//    override suspend fun deleteWishItem(itemId: Long): Result<Boolean> = runCatching {
//        wishItemService.deleteWishItem(itemId)
//    }.fold({
//        Timber.d("아이템 삭제 성공(${it.code()})")
//        it.isSuccessful
//    }, {
//        Timber.e("아이템 삭제 실패: ${it.message}")
//        false
//    })
//
//    // TODO need refactoring
//    override suspend fun getItemParsingInfo(site: String): Result<Pair<ItemInfo?, Int>?> {
//        return runCatching {
//            wishItemService.getItemParsingInfo(site)
//        }.fold({
//            Timber.d("아이템 파싱 성공(${it.code()})")
//            Pair(it.body()?.data, it.code())
//        }, {
//            Timber.d("아이템 파싱 실패: ${it.message}")
//            null
//        })
//    }
}
