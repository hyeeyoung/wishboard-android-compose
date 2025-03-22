package com.hyeeyoung.wishboard.data.util

import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

class ContentUriRequestBody(val name: String, private val imageFile: ImageType.Picture) :
    RequestBody() {

    override fun contentLength(): Long = imageFile.size

    override fun contentType(): MediaType? =
        imageFile.mimeType?.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        imageFile.inputStreamProvider()?.source()?.use { source ->
            sink.writeAll(source)
        }
    }

    fun toFormData(): MultipartBody.Part =
        MultipartBody.Part.createFormData(name, imageFile.name, this)
}
