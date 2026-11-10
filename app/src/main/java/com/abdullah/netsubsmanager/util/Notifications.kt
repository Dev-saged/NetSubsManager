package com.abdullah.netsubsmanager.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.abdullah.netsubsmanager.R

private const val CHANNEL_ID = "subs_alerts"

fun ensureChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notif_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notif_channel_desc)
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }
}

fun scheduleExpiryReminder(context: Context, assignmentId: Long, customerName: String, whenAt: Long) {
    ensureChannel(context)
    val intent = Intent(context, ExpiryReceiver::class.java).apply {
        putExtra("assignmentId", assignmentId)
        putExtra("customerName", customerName)
    }
    val pi = PendingIntent.getBroadcast(
        context,
        assignmentId.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenAt, pi)
}

class ExpiryReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("assignmentId", 0L)
        val name = intent.getStringExtra("customerName") ?: "مشترك"
        ensureChannel(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("ينتهي اشتراك $name")
            .setContentText("تذكير: الاشتراك سينتهي اليوم.")
            .setAutoCancel(true)
            .build()
        nm.notify(id.toInt(), notif)
    }
}
