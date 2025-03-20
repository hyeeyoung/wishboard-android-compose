package com.hyeeyoung.wishboard.data.remote.model.folder

import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FolderItemDto(
    @SerialName("folder_id")
    val id: Long,
    @SerialName("folder_name")
    var name: String = "",
    @SerialName("folder_thumbnail")
    val thumbnail: String = "",
    @SerialName("item_count")
    val numOfWishItem: Int = 0,
) {
    fun toDomain(): FolderItem = FolderItem(
        id = id,
        name = name,
        thumbnail = thumbnail,
        numOfWishItem = numOfWishItem,
    )
}
