package com.hyeeyoung.wishboard.presentation.upload.model

import android.net.Uri
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.presentation.util.WishBoardDateFormat.getFormattedDateStr
import kotlinx.datetime.LocalDateTime

data class WishItemUploadUiModel(
    val selectedFolder: FolderItem? = null,
    val itemName: String = "",
    val itemPrice: String = "",
    val itemUrl: String = "",
    val itemNotiType: NotiType? = null,
    val itemNotiDate: LocalDateTime? = null,
    /** 파싱 혹은 아이템 수정(기존 이미지) 시점에서 사용 */
    val downloadImageUrl: String? = null,
    /** 카메라 혹은 갤러리 이미지, 아이템 등록 중 사용(실시간) */
    val itemImageUri: Uri? = null,
    val itemMemo: String = "", // TODO 빈문자열 테스트
    val folders: List<FolderItem> = emptyList(),
    val folderFetchState: WishBoardState<Unit> = WishBoardState.Idle,
    val folderAddState: WishBoardState<Unit> = WishBoardState.Idle,
    val existingFolderName: String? = null,
    val isLogin: Boolean = true,
    val wishItemUploadState: WishBoardState<Unit> = WishBoardState.Idle,
) {
    fun toDomain(itemImage: ImageType?): WishItemUploadInfo {
        val dateStr = itemNotiDate?.getFormattedDateStr(WishBoardDateFormat.YYYY_MM_DD_HH_MM_SS)

        return WishItemUploadInfo(
            folderId = selectedFolder?.id,
            itemName = itemName.trim(),
            itemPrice = itemPrice.replace(",", "").toIntOrNull(),
            itemUrl = itemUrl.trim(),
            itemMemo = itemMemo.trim(),
            itemNotiType = itemNotiType,
            itemNotiDate = dateStr,
            itemImage = itemImage,
        )
    }
}
