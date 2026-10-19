package com.hyeeyoung.wishboard.data.remote.model.folder

import com.hyeeyoung.wishboard.domain.model.folder.FolderThumbnailImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FolderThumbnailImageDto(
    @SerialName("itemId")
    val itemId: Long,
    @SerialName("itemImageId")
    val itemImageId: Long,
    @SerialName("itemImageUrl")
    val itemImageUrl: String = "",
    @SerialName("selected")
    val selected: Boolean = false,
) {
    fun toDomain() = FolderThumbnailImage(
        itemId = itemId,
        itemImageId = itemImageId,
        imageUrl = itemImageUrl,
        isSelected = selected,
    )
}
