package com.hyeeyoung.wishboard.data.remote.model.folder

import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FolderSummaryDto(
    @SerialName("id")
    val id: Long,
    @SerialName("folderName")
    var name: String = "",
    @SerialName("folderThumbnail")
    val thumbnail: String = "",
) {
    fun toDomain(): FolderItem = FolderItem(
        id = id,
        name = name,
        thumbnail = thumbnail,
    )
}
