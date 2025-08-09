package com.hyeeyoung.wishboard.presentation.util.extension

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Context.sendMail(title: String, content: String) {
    Intent(Intent.ACTION_SEND).apply {
        type = "plain/text"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.wishboard_email)))
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
    }.also { startActivity(it) }
}

fun Context.createImageUri(token: String): Uri { // TODO 파일 위치 변경 고려
    val timeStamp = getTimestamp()
    val tokenPrefix = if (token.length >= 7) "${token.substring(0, 7)}_" else ""
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    val file = File.createTempFile("$tokenPrefix$timeStamp", ".jpg", storageDir)

    return FileProvider.getUriForFile(
        this,
        BuildConfig.FILE_PROVIDER,
        file,
    )
}

private fun getTimestamp(): String {
    val currentTime = LocalDateTime.now(ZoneId.of("UTC"))
    return currentTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"))
}

fun Context.compressImageToMaxSize(
    imageUri: Uri,
    maxSizeBytes: Int = 1 * 1024 * 1024,
    maxDimension: Int = 1080,
): File? {
    val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
    } else {
        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
    } ?: return null

    val resizedBitmap = resizeBitmapMaintainingAspectRatio(originalBitmap, maxDimension)

    var quality = 100
    var compressedBytes: ByteArray

    do {
        val baos = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        compressedBytes = baos.toByteArray()
        quality -= 5
    } while (compressedBytes.size > maxSizeBytes && quality > 5)

    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "compressed_image.jpg")
    try {
        FileOutputStream(file).use { it.write(compressedBytes) }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

private fun resizeBitmapMaintainingAspectRatio(bitmap: Bitmap, maxSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val ratio = width.toFloat() / height.toFloat()

    return if (width > height) {
        if (width <= maxSize) return bitmap
        bitmap.scale(maxSize, (maxSize / ratio).toInt())
    } else {
        if (height <= maxSize) return bitmap
        bitmap.scale((maxSize * ratio).toInt(), maxSize)
    }
}
