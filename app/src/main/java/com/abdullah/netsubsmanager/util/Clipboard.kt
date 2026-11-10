package com.abdullah.netsubsmanager.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("msg", text))
    Toast.makeText(context, "تم النسخ", Toast.LENGTH_SHORT).show()
}
