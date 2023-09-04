package com.hyeeyoung.wishboard.presentation.util.extension

import android.content.Context
import android.content.Intent
import com.hyeeyoung.wishboard.R

fun Context.sendMail(title: String, content: String) {
    Intent(Intent.ACTION_SEND).apply {
        type = "plain/text"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.wishboard_email)))
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
    }.also { startActivity(it) }
}
