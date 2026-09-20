package com.hyeeyoung.wishboard.data.remote.model.folder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReorderFolderListDto(
    @SerialName("folderIds")
    val ids: List<Long>,
)
