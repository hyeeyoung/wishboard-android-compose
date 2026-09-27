package com.hyeeyoung.wishboard.presentation.wish.model

import com.hyeeyoung.wishboard.data.util.extension.toInstantToLocalDateTime
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemDetail
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WishItemDetailUiModel(
    val id: Long = 0L,
    var folderId: Long? = null,
    var folderName: String? = "",
    var images: List<String> = emptyList(),
    val memo: String? = null,
    val name: String = "",
    val notiDate: LocalDateTime? = null,
    val notiType: NotiType? = null,
    val price: Long = 0L,
    val site: String? = null,
    val createAt: LocalDateTime? = null,
    val version: Int = 0,
    val isOwnedItem: Boolean = false,
    val isEditingMemo: Boolean = false,
    val memoInput: String = "",
    // 수정 화면으로 넘어갈 때 직렬화되는 값이 아니라 화면 내부에서만 쓰는 로딩 상태라 직렬화 대상에서 제외한다.
    @Transient
    val fetchState: WishBoardState<Unit> = WishBoardState.Loading,
) {
    companion object {
        fun fromDomain(domain: WishItemDetail): WishItemDetailUiModel =
            WishItemDetailUiModel(
                id = domain.id,
                folderId = domain.folderId,
                folderName = domain.folderName,
                images = domain.images,
                memo = domain.memo,
                name = domain.name,
                notiDate = domain.notiDate?.toInstantToLocalDateTime(),
                notiType = domain.notiType,
                price = domain.price.toLongOrNull() ?: 0,
                site = domain.site,
                createAt = domain.createAt.toInstantToLocalDateTime(),
                version = domain.version,
                isOwnedItem = domain.isOwnedItem,
                fetchState = WishBoardState.Success(Unit),
            )
    }
}
