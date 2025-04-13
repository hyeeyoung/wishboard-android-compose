package com.hyeeyoung.wishboard.data.remote.repository

import com.hyeeyoung.wishboard.data.remote.service.ItemService
import com.hyeeyoung.wishboard.data.util.extension.toPlainNullableRequestBody
import com.hyeeyoung.wishboard.data.util.extension.toPlainRequestBody
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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

    override suspend fun uploadWishItem(uploadType: WishItemUploadType, itemInfo: WishItemUploadInfo): Result<Long> =
        runCatching {
            val formDataName = "item_img"

            itemService.uploadWishItem(
                type = uploadType.toString(),
                folderId = itemInfo.folderId?.toString()?.toPlainNullableRequestBody(),
                itemName = itemInfo.itemName.toPlainRequestBody(),
                itemPrice = itemInfo.itemPrice?.toString()?.toPlainNullableRequestBody(),
                itemMemo = itemInfo.itemMemo.toPlainNullableRequestBody(),
                itemNotificationDate = itemInfo.itemNotiDate?.toPlainNullableRequestBody(),
                itemNotificationType = itemInfo.itemNotiType?.label?.toPlainNullableRequestBody(),
                itemUrl = itemInfo.itemUrl.toPlainNullableRequestBody(),
                itemImg = when (itemInfo.itemImage) {
                    is ImageType.DownloadImage -> {
                        MultipartBody.Part.createFormData(
                            formDataName,
                            itemInfo.itemImage.file.name,
                            itemInfo.itemImage.file.asRequestBody(),
                        )
                    }

                    is ImageType.Picture -> itemInfo.itemImage.image

                    else -> null
                },
            ).data.id
        }

    override suspend fun updateWishItem(
        itemId: Long,
        itemInfo: WishItemUploadInfo,
    ): Result<Unit> = runCatching {
        val formDataName = "item_img"

        itemService.updateWishItem(
            itemId = itemId,
            folderId = itemInfo.folderId?.toString()?.toPlainNullableRequestBody(),
            itemName = itemInfo.itemName.toPlainRequestBody(),
            itemPrice = itemInfo.itemPrice?.toString()?.toPlainNullableRequestBody(),
            itemMemo = itemInfo.itemMemo.toPlainNullableRequestBody(),
            itemNotificationDate = itemInfo.itemNotiDate?.toPlainNullableRequestBody(),
            itemNotificationType = itemInfo.itemNotiType?.label?.toPlainNullableRequestBody(),
            itemUrl = itemInfo.itemUrl.toPlainNullableRequestBody(),
            itemImg = when (itemInfo.itemImage) {
                is ImageType.DownloadImage -> {
                    MultipartBody.Part.createFormData(
                        formDataName,
                        itemInfo.itemImage.file.name,
                        itemInfo.itemImage.file.asRequestBody(),
                    )
                }

                is ImageType.Picture -> itemInfo.itemImage.image

                else -> null
            },
        )
    }

    override suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Result<Unit> =
        runCatching {
            itemService.updateFolderOfItem(itemId, folderId)
        }

    override suspend fun deleteWishItem(itemId: Long): Result<Unit> = runCatching {
        itemService.deleteWishItem(itemId)
    }

    override suspend fun getParsedItemInfo(site: String): Result<ParsedWishItem> =
        runCatching {
            itemService.getParsedItemInfo(site).data
        }
}
