package com.hyeeyoung.wishboard.presentation.common.model

import java.io.File
import java.io.InputStream

sealed class ImageType {
    data class Picture(
        val name: String,
        val size: Long,
        val mimeType: String?,
        val inputStreamProvider: () -> InputStream?
    ) : ImageType()

    data class DownloadImage(
        val file: File
    ) : ImageType()
}
