package com.hyeeyoung.wishboard.data.remote.model.folder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FolderNameDto(
    @SerialName("folderName")
    var name: String = "",
)
