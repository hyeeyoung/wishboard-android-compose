package com.hyeeyoung.wishboard.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hyeeyoung.wishboard.data.remote.model.common.PageSize
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemUploadInfoDto
import com.hyeeyoung.wishboard.data.remote.paging.WishItemPagingSource
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
import com.hyeeyoung.wishboard.presentation.util.extension.toJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemService: ItemService,
) : ItemRepository {
    override fun fetchWishList(): Flow<PagingData<WishItem>> =
        Pager(
            config = PagingConfig(
                pageSize = PageSize.DEFAULT_SIZE,
                enablePlaceholders = true,
                prefetchDistance = PageSize.DEFAULT_PREFETCH_SIZE,
            ),
            pagingSourceFactory = {
                WishItemPagingSource(itemService)
            },
        ).flow.map {
            Timber.e(it.toString())
            it.map { it.toDomain() }
        }

    override suspend fun fetchWishItemDetail(itemId: Long): Result<WishItemDetail> =
        runCatching {
            itemService.fetchWishItemDetail(itemId).data.toDomain()
        }

    override suspend fun uploadWishItem(uploadType: WishItemUploadType, itemInfo: WishItemUploadInfo): Result<Long> =
        runCatching {
            val formDataName = "itemImages"

            itemService.uploadWishItem(
                type = uploadType.toString(),
                item = WishItemUploadInfoDto.fromDomain(itemInfo).toJson().toRequestBody(
                    "application/json".toMediaTypeOrNull(),
                ),
                itemImg = itemInfo.itemImage?.mapNotNull {
                    when (it) {
                        is ImageType.DownloadImage -> {
                            MultipartBody.Part.createFormData(
                                formDataName,
                                it.file.name,
                                it.file.asRequestBody(),
                            )
                        }

                        is ImageType.Picture -> it.image
                    }
                },
            ).data.id
        }

    override suspend fun updateWishItem(
        itemId: Long,
        itemInfo: WishItemUploadInfo,
    ): Result<Unit> = runCatching {
        val formDataName = "itemImages"

        itemService.updateWishItem(
            itemId = itemId,
            folderId = itemInfo.folderId?.toString()?.toPlainNullableRequestBody(),
            itemName = itemInfo.itemName.toPlainRequestBody(),
            itemPrice = itemInfo.itemPrice?.toString()?.toPlainNullableRequestBody(),
            itemMemo = itemInfo.itemMemo.toPlainNullableRequestBody(),
            itemNotificationDate = itemInfo.itemNotiDate?.toPlainNullableRequestBody(),
            itemNotificationType = itemInfo.itemNotiType?.label?.toPlainNullableRequestBody(),
            itemUrl = itemInfo.itemUrl.toPlainNullableRequestBody(),
            itemImg = itemInfo.itemImage?.mapNotNull {
                when (it) {
                    is ImageType.DownloadImage -> {
                        MultipartBody.Part.createFormData(
                            formDataName,
                            it.file.name,
                            it.file.asRequestBody(),
                        )
                    }

                    is ImageType.Picture -> it.image

                    else -> null
                }
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
