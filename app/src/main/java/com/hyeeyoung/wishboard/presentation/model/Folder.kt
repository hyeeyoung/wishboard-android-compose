package com.hyeeyoung.wishboard.presentation.model

data class Folder(
    val id: Long,
    val name: String,
    val thumbnail: String,
    var itemCount: Int,
)
