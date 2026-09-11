package com.hyeeyoung.wishboard.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hyeeyoung.wishboard.data.remote.model.common.PageSize
import com.hyeeyoung.wishboard.domain.model.wish.WishItemOwnershipStatus
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemOwnershipRequestDto
import com.hyeeyoung.wishboard.data.remote.model.wish.WishItemUploadInfoDto
import com.hyeeyoung.wishboard.data.remote.paging.GeneralPagingSource
import com.hyeeyoung.wishboard.data.remote.service.ItemService
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemCount
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import com.hyeeyoung.wishboard.presentation.util.extension.toJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemService: ItemService,
) : ItemRepository {
    private val _totalElements = MutableStateFlow<Int?>(null)
    override val totalElements: StateFlow<Int?> = _totalElements.asStateFlow()

    override fun fetchWishList(itemStatus: WishItemOwnershipStatus?): Flow<PagingData<WishItem>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = PageSize.DEFAULT_SIZE,
                pageSize = PageSize.DEFAULT_SIZE,
                enablePlaceholders = false,
                prefetchDistance = PageSize.DEFAULT_PREFETCH_SIZE,
            ),
            pagingSourceFactory = {
                GeneralPagingSource(
                    loadPage = { page, size ->
                        itemService.fetchWishList(page = page, size = size, itemStatus = itemStatus?.name)
                    },
                    onMetaLoaded = { totalElements -> _totalElements.value = totalElements },
                )
            },
        ).flow.map {
            it.map { it.toDomain() }
        }

    override suspend fun fetchWishItemDetail(itemId: Long): Result<WishItemDetail> =
        runCatching {
            itemService.fetchWishItemDetail(itemId).data.toDomain()
        }

    override suspend fun uploadWishItem(uploadType: WishItemUploadType, itemInfo: WishItemUploadInfo): Result<Long> =
        runCatching {
            val formDataName = FORM_DATA_IMAGE_KEY

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
                                it.file.asRequestBody("image/jpeg".toMediaTypeOrNull()),
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
        itemService.updateWishItem(
            itemId = itemId,
            item = json.encodeToString(WishItemUploadInfoDto.fromDomain(itemInfo)).toRequestBody(
                "application/json".toMediaTypeOrNull(), // TODO 상수화 필요
            ),
            itemImg = itemInfo.itemImage?.mapNotNull {
                when (it) {
                    is ImageType.DownloadImage -> {
                        MultipartBody.Part.createFormData(
                            FORM_DATA_IMAGE_KEY,
                            it.file.name,
                            it.file.asRequestBody("image/jpeg".toMediaTypeOrNull()),
                        )
                    }

                    is ImageType.Picture -> it.image
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

    override suspend fun getParsedItemInfo(site: String): Result<ParsedWishItem?> =
        runCatching {
            itemService.getParsedItemInfo(site).data
        }

    override suspend fun updateItemOwnership(itemId: Long, isOwnedItem: Boolean): Result<Boolean> =
        runCatching {
            itemService.updateItemOwnership(
                itemId = itemId,
                ownership = WishItemOwnershipRequestDto(
                    status = (if (isOwnedItem) WishItemOwnershipStatus.OWNED else WishItemOwnershipStatus.WISH).name,
                ),
            ).data.itemStatus == WishItemOwnershipStatus.OWNED.name
        }

    override suspend fun getItemCount(): Result<WishItemCount> = runCatching {
        itemService.getItemCount().data
    }

    companion object {
        private const val FORM_DATA_IMAGE_KEY = "itemImages"

        // TODO 제거 예정
        val json = Json {
            isLenient = true
            prettyPrint = true
            explicitNulls = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            serializersModule = SerializersModule {
                contextual(InstantIso8601Serializer)
                contextual(LocalDateTimeIso8601Serializer)
            }
        }
    }
}
