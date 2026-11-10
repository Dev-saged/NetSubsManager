package com.abdullah.netsubsmanager.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun sendSms(context: Context, phone: String, body: String) {
    val uri = Uri.parse("smsto:$phone")
    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra("sms_body", body)
    }
    context.startActivity(intent)
}
