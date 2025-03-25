package com.hyeeyoung.wishboard.presentation.util.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.hyeeyoung.wishboard.domain.util.WishBoardDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    fun Bitmap.compressImage(format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): ByteArray {
        var quality = DEFAULT_IMAGE_QUALITY
        var outputStream: ByteArrayOutputStream
        var attempt = 0
        val maxAttempts = 20

        do {
            outputStream = ByteArrayOutputStream()
            this.compress(format, quality, outputStream)
            quality -= IMAGE_COMPRESSION_DECREASE_FACTOR
            attempt++
        } while (outputStream.size() > MAXIMUM_IMAGE_SIZE && quality > IMAGE_COMPRESSION_DECREASE_FACTOR && attempt < maxAttempts)

        return outputStream.toByteArray()
    }
}
