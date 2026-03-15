package com.hyeeyoung.wishboard.data.remote.model.folder

import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FolderItemDto(
    @SerialName("id")
    val id: Long,
    @SerialName("folderName")
    var name: String = "",
    @SerialName("folderThumbnail")
    val thumbnail: String = "",
    @SerialName("itemCount")
    val numOfWishItem: Int = 0,
) {
    fun toDomain(): FolderItem = FolderItem(
        id = id,
        name = name,
        thumbnail = thumbnail,
        numOfWishItem = numOfWishItem,
    )
}
