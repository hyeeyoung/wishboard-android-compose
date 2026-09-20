package com.hyeeyoung.wishboard.presentation.common.model

import okhttp3.MultipartBody
import java.io.File

sealed class ImageType {
    data class Picture(
        val image: MultipartBody.Part?,
    ) : ImageType()

    data class DownloadImage(
        val file: File,
    ) : ImageType()
}
