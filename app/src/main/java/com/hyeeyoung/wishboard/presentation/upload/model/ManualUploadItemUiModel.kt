package com.hyeeyoung.wishboard.presentation.upload.model

import androidx.compose.ui.text.input.TextFieldValue
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.toUtcFormattedString
import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import kotlinx.datetime.LocalDateTime

data class ManualUploadItemUiModel(
    val accessToken: String = "",
    val selectedFolder: FolderItem? = null,
    val itemName: TextFieldValue = TextFieldValue(),
    val itemPrice: TextFieldValue = TextFieldValue(),
    val itemUrl: TextFieldValue = TextFieldValue(),
    val itemNotiType: NotiType? = null,
    val itemNotiDate: LocalDateTime? = null,
    val images: List<UploadImage> = emptyList(),
    /** 수정 화면 진입 시점의 원본 이미지 목록(추가/삭제/순서 변경 여부를 판단하는 기준) */
    val originalImages: List<UploadImage> = emptyList(),
    val itemMemo: TextFieldValue = TextFieldValue(),
    val wishItemUploadState: WishBoardState<Unit> = WishBoardState.Idle,
    val folders: List<FolderItem> = emptyList(),
    val existingFolderName: String? = null,
    val folderFetchState: WishBoardState<Unit> = WishBoardState.Idle,
    val folderAddState: WishBoardState<Unit> = WishBoardState.Idle,
    val version: Int = 0,
) {
    fun toDomain(itemImage: List<ImageType>): WishItemUploadInfo {
        val dateStr = itemNotiDate?.toUtcFormattedString(WishBoardDateFormat.YYYY_MM_DD_HH_MM_SS)

        return WishItemUploadInfo(
            folderId = selectedFolder?.id,
            itemName = itemName.text.trim(),
            itemPrice = itemPrice.text.replace(",", "").toIntOrNull(),
            itemUrl = itemUrl.text.ifEmpty { null }?.trim(),
            itemMemo = itemMemo.text.trim(),
            itemNotiType = itemNotiType,
            itemNotiDate = dateStr,
            itemImage = itemImage,
            // 새 이미지 추가뿐 아니라 삭제나 순서 변경도 "변경"으로 취급해야 하므로,
            // 새로 첨부된 이미지 유무가 아니라 원본 목록과의 전체 비교(순서 포함)로 판단한다.
            updateInfo = WishItemUploadInfo.UpdateInfo(
                version = version,
                imageChanged = images != originalImages,
            ),
        )
    }
}
