package com.hyeeyoung.wishboard.data.remote.model.wish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishItemImage(
    @SerialName("itemImageUrl")
    val url: String,
    @SerialName("itemImg")
    val itemImg: String, // TODO 제거
)
