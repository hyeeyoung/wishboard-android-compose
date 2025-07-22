package com.hyeeyoung.wishboard.presentation.upload.model

import androidx.compose.ui.text.input.TextFieldValue
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.wish.WishItemUploadInfo
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat.getFormattedDateStr
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
    val itemMemo: TextFieldValue = TextFieldValue(),
    val wishItemUploadState: WishBoardState<Unit> = WishBoardState.Idle,
    val folders: List<FolderItem> = emptyList(),
    val existingFolderName: String? = null,
    val folderFetchState: WishBoardState<Unit> = WishBoardState.Idle,
    val folderAddState: WishBoardState<Unit> = WishBoardState.Idle,
) {
    fun toDomain(itemImage: List<ImageType>): WishItemUploadInfo {
        val dateStr = itemNotiDate?.getFormattedDateStr(WishBoardDateFormat.YYYY_MM_DD_HH_MM_SS)

        return WishItemUploadInfo(
            folderId = selectedFolder?.id,
            itemName = itemName.text.trim(),
            itemPrice = itemPrice.text.replace(",", "").toIntOrNull(),
            itemUrl = itemUrl.text.ifEmpty { null }?.trim(),
            itemMemo = itemMemo.text.trim(),
            itemNotiType = itemNotiType,
            itemNotiDate = dateStr,
            itemImage = itemImage,
        )
    }
}
