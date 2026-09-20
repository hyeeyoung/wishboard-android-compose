package com.hyeeyoung.wishboard.presentation.upload.model

import android.net.Uri
import java.util.UUID

sealed class UploadImage(val id: String = UUID.randomUUID().toString()) {
    data class Remote(val url: String) : UploadImage()
    data class Local(val uri: Uri) : UploadImage()
}
