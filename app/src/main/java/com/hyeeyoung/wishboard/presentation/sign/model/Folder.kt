package com.hyeeyoung.wishboard.presentation.sign.model

data class Folder(
    val id: Long,
    val name: String,
    val thumbnail: String,
    var itemCount: Int,
)
