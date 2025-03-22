package com.hyeeyoung.wishboard.presentation.util.extension

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.hyeeyoung.wishboard.presentation.common.model.ImageType
import com.hyeeyoung.wishboard.presentation.util.WishBoardDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object BitmapUtil {
    private const val DEFAULT_IMAGE_QUALITY = 100
    private const val IMAGE_COMPRESSION_DECREASE_FACTOR = 5
    private const val MAXIMUM_IMAGE_SIZE = 4 * 1024 * 1024
    private const val DEFAULT_MIME_TYPE = "image/jpeg"

    /** 다운로드 이미지 url -> bitmap -> file 변환 */
    suspend fun String.toBitmap(): Bitmap? = runCatching {
        val url = URL(this)

        withContext(Dispatchers.IO) {
            val connection = (url.openConnection() as? HttpURLConnection)?.apply {
                doInput = true
                connect()
            }

            connection?.let {
                val bitmap = BitmapFactory.decodeStream(it.inputStream)

                if (getImageSizeInBytes(bitmap) > MAXIMUM_IMAGE_SIZE) {
                    val compressedByteArray = bitmap.compressImage()
                    return@withContext BitmapFactory.decodeByteArray(
                        compressedByteArray,
                        0,
                        compressedByteArray.size
                    )
                }

                return@withContext bitmap
            }
        }
    }.onFailure { Timber.e(it) }.getOrNull()

    fun Bitmap.toFile(token: String, context: Context): File? = runCatching {
        val file = File(context.cacheDir, makeFileName(token))
        Timber.d("파일 경로: ${file.absolutePath}")

        FileOutputStream(file).use { outputStream ->
            this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        }

        file
    }.onFailure { Timber.e(it) }.getOrNull()

    private fun getImageSizeInBytes(bitmap: Bitmap): Long {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.size().toLong()
    }

    private fun makeFileName(token: String): String {
        val timestamp = getTimestamp()
        return ("${token.substring(7)}_${timestamp}.jpg")
    }

    private fun getTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern(WishBoardDateFormat.YYYYMMDD_T_HHMMSS_Z)
            .withZone(ZoneOffset.UTC)
        return formatter.format(Instant.now())
    }

    /** 갤러리 & 촬영 이미지를 파일로 변환 */
    fun Uri.toImageFile(contentResolver: ContentResolver): ImageType.Picture {
        var name = "unknown.jpg"
        var size = -1L
        val mimeType = contentResolver.getType(this)

        Timber.e("들어옴1")

        contentResolver.query(
            this,
            arrayOf(MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
            }
        }

        return ImageType.Picture(name = name, size = size, mimeType = mimeType) {
            if (size > MAXIMUM_IMAGE_SIZE) {
                Timber.e("hello1")
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(this))
                val format =
                    if (mimeType == DEFAULT_MIME_TYPE) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG
                val compressedByteArray = bitmap.compressImage(format)
                ByteArrayInputStream(compressedByteArray)
            } else {
                Timber.e("hello2")
                contentResolver.openInputStream(this)
            }
        }
    }

    private fun Bitmap.compressImage(format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): ByteArray {
        var quality = DEFAULT_IMAGE_QUALITY
        val outputStream = ByteArrayOutputStream()

        do {
            outputStream.reset()
            this.compress(format, quality, outputStream)
            quality -= IMAGE_COMPRESSION_DECREASE_FACTOR
        } while (outputStream.size() > MAXIMUM_IMAGE_SIZE && quality > IMAGE_COMPRESSION_DECREASE_FACTOR)

        Timber.e("size : ${outputStream.size().toFloat() / (1024 * 1024)}")
        return outputStream.toByteArray()
    }
}
