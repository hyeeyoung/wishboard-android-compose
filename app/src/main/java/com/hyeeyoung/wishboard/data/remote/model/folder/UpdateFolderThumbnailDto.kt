package com.hyeeyoung.wishboard.data.remote.model.folder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateFolderThumbnailDto(
    @SerialName("itemImageId")
    val itemImageId: Long,
)
