package com.hyeeyoung.wishboard.presentation.model

import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class WishItemDetail(
    val id: Long,
    val name: String,
    var image: String,
    val price: Int,
    val notiDate: LocalDateTime? = null,
    val notiType: NotiType? = null,
    val site: String? = null,
    val memo: String? = null,
    var folderId: Long? = null,
    var folderName: String? = null,
    val createAt: String,
) {
    @Serializable
    data class SerializableDetail(
        val id: Long,
        val name: String,
        var image: String,
        val price: Int,
        val notiDate: Long? = null,
        val notiType: NotiType? = null,
        val site: String? = null,
        val memo: String? = null,
        var folderId: Long? = null,
        var folderName: String? = null,
        val createAt: String,
    ) {
        fun toWishItemDetail() = WishItemDetail(
            id = id,
            name = name,
            image = image,
            price = price,
            notiDate = if (notiDate == null) {
                null
            } else {
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(notiDate),
                    ZoneId.of("Asia/Seoul"),
                )
            },
            notiType = notiType,
            site = site,
            memo = memo,
            folderId = folderId,
            folderName = folderName,
            createAt,
        )
    }

    fun toSerializable() = SerializableDetail(
        id = id,
        name = name,
        image = image,
        price = price,
        notiDate = notiDate?.atZone(ZoneId.of("Asia/Seoul"))?.toInstant()?.toEpochMilli(),
        notiType = notiType,
        site = site,
        memo = memo,
        folderId = folderId,
        folderName = folderName,
        createAt,
    )
}
