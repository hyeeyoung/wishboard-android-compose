package com.hyeeyoung.wishboard.data.remote.repository

import com.hyeeyoung.wishboard.data.remote.service.NotiService
import com.hyeeyoung.wishboard.data.util.ContentUriRequestBody
import com.hyeeyoung.wishboard.data.util.extension.toPlainNullableRequestBody
import com.hyeeyoung.wishboard.data.util.extension.toPlainRequestBody
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.domain.repository.NotiRepository
import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class NotiRepositoryImpl @Inject constructor(
    private val notiService: NotiService,
) : NotiRepository {
    override suspend fun fetchPreviousNotiList(): Result<List<NotiItem>> = runCatching {
        notiService.fetchPreviousNotiList().map { it.toDomain() }
    }

    override suspend fun fetchAllNotiList(): Result<List<NotiItem>> = runCatching {
        notiService.fetchAllNotiList().map { it.toDomain() }
    }

    override suspend fun updateNotiReadState(itemId: Long): Result<Unit> = runCatching {
        notiService.updateNotiReadState(itemId = itemId)
    }
}
