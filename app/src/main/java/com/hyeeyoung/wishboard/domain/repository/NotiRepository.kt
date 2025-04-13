package com.hyeeyoung.wishboard.domain.repository

import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem

interface NotiRepository {
    suspend fun fetchPreviousNotiList(): Result<List<NotiItem>>

    suspend fun fetchAllNotiList(): Result<List<NotiItem>>

    suspend fun updateNotiReadState(itemId: Long): Result<Unit>
}
