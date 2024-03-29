package com.church.injilkeselamatan.audio_domain.use_case

import android.content.Context
import android.content.Intent
import android.net.Uri

class EmailIntent(private val context: Context) {

    operator fun invoke() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:oasisjiwa2022@gmail.com")
            putExtra(Intent.EXTRA_EMAIL, "oasisjiwa2022@gmail.com")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}