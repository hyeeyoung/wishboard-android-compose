package com.hyeeyoung.wishboard.presentation.upload

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class LinkSharingWishUploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var url = ""
        if (intent.action == Intent.ACTION_SEND) {
            if (intent.type == "text/plain") {
                url = intent.getStringExtra(Intent.EXTRA_TEXT) ?: throw NullPointerException("Url is null")
                // TODO 서버 연동
            }
        }

        setContent {
            if (url.isEmpty()) return@setContent
            LinkSharingWishUploadScreen(url = url, onClickClose = { finish() })
        }
    }
}
