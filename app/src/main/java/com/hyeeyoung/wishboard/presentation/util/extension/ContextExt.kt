package com.hyeeyoung.wishboard.presentation.util.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import java.io.File
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
    val tokenPrefix = if (token.length >= 7) "_${token.substring(0, 7)}" else ""
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    val file = File.createTempFile(
        "$timeStamp$tokenPrefix",
        ".jpg",
        storageDir,
    )

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
