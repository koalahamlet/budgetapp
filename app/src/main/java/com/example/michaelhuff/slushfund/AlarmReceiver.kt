package com.example.michaelhuff.slushfund

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.example.michaelhuff.slushfund.Constants.DAILY_ALLOWANCE
import com.example.michaelhuff.slushfund.Constants.SLUSH_KEY

class AlarmReceiver : BroadcastReceiver() {

    val PREFS_FILENAME = "com.michaelhuff.slushfund.prefs"

    override fun onReceive(context: Context, intent: Intent) {

        val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)

        var slush = prefs.getLong(SLUSH_KEY, 0)

        slush = slush + DAILY_ALLOWANCE

        prefs.edit().putLong(SLUSH_KEY, slush).apply()

        // Send Notification
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(context, MainActivity::class.java)

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .addAction(NotificationCompat.Action(R.mipmap.ic_launcher, "action!", ))
                .setContentTitle("Slush Fund Increased")
                .setContentText("Another day, another $$$")
                .setSound(alarmSound)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(200, 500, 500, 200, 1000)) as NotificationCompat.Builder
        notificationManager.notify(0, builder.build())

    }
}

