package com.hyeeyoung.wishboard.domain.model.folder

import java.io.Serializable

data class FolderItem(
    val id: Long,
    var name: String = "",
    val thumbnail: String = "",
    val numOfWishItem: Int = 0,
) : Serializable
