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
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import timber.log.Timber
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

fun Context.convertResizeImage(imageUri: Uri): File? {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
    } else {
        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
    } ?: throw IllegalArgumentException("Bitmap decoding failed")

    val file =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "compressed_image.jpg")
    return try {
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos)
        fos.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
