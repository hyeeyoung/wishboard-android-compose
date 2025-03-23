package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponseWithoutData
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadType
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotiRepository {
    suspend fun fetchPreviousNotiList(): Result<List<NotiItem>>

    suspend fun fetchAllNotiList(): Result<List<NotiItem>>

    suspend fun updateNotiReadState(itemId: Long): Result<Unit>
}
