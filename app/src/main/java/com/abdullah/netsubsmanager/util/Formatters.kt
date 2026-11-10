package com.abdullah.netsubsmanager.util

import java.util.concurrent.TimeUnit

fun defaultMessageTemplate(): String = """
أهلاً وسهلاً، يوزر شهري
اليوزر: {USERNAME}
كلمة السر: {PASSWORD}
الحالة: {PAID_STATUS}
ينتهي في: {EXPIRY_DATE}
""".trimIndent()

fun renderTemplate(template: String, username: String, password: String, paid: Boolean, expiryDate: String): String {
    return template
        .replace("{USERNAME}", username)
        .replace("{PASSWORD}", password)
        .replace("{PAID_STATUS}", if (paid) "مدفوع" else "غير مدفوع")
        .replace("{EXPIRY_DATE}", expiryDate)
}

fun daysRemaining(now: Long, expiryAt: Long): Long {
    val diff = expiryAt - now
    return if (diff <= 0L) 0L else TimeUnit.MILLISECONDS.toDays(diff)
}
