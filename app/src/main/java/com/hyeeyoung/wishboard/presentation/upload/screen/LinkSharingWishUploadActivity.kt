package com.hyeeyoung.wishboard.presentation.upload.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setNavigationBarColor(Color.White)
            }

            if (url.isEmpty()) return@setContent
            LinkSharingWishUploadScreen(url = url, onClickClose = { finish() })
        }
    }
}
