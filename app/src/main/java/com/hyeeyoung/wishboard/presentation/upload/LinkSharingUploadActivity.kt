package com.hyeeyoung.wishboard.presentation.upload

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class LinkSharingUploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var url: String = ""
        if (intent.action == Intent.ACTION_SEND) { // TODO 타입 널체크 앤해도 되는지 확인
            if (intent.type == "text/plain") {
                url = intent.getStringExtra(Intent.EXTRA_TEXT) ?: throw NullPointerException("Url is null")
                // TODO 서버 연동
            }
        }

        setContent {
            if (url.isEmpty()) return@setContent
            WishItemLinkSharingUploadScreen(url = url, onClickClose = { finish() })
        }
    }
}
