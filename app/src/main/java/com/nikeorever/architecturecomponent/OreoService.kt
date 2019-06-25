package com.nikeorever.architecturecomponent

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

fun notification(context: MyIntentService) {

    val icon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

    val intent = Intent(context, SecondActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

    val notificationBuilder: NotificationCompat.Builder
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationBuilder = NotificationCompat.Builder(context, "OreoServiceChannel")

        val notificationChannel =
            NotificationChannel("OreoServiceChannel", "Tag", NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableVibration(true)

        val notificationManager = context.getSystemService<NotificationManager>()!!
        notificationManager.createNotificationChannel(notificationChannel)
    } else {
        notificationBuilder = NotificationCompat.Builder(context)
    }
    notificationBuilder.setContentTitle("contentTitle")
        .setContentText("contentText")
        .setAutoCancel(true)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setLargeIcon(icon)
        .setContentIntent(pendingIntent)
        .setColor(Color.RED)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setDefaults(Notification.DEFAULT_VIBRATE)
        .setLights(Color.YELLOW, 1000, 300)

    val notificationManager = context.getSystemService<NotificationManager>()!!
    notificationManager.notify(0, notificationBuilder.build())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel("OreoServiceChannel", "Tag", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(context.applicationContext, "OreoServiceChannel").build()
        context.startForeground(1, notification)
    } else {

    }
}

class MyIntentService : IntentService("MyIntentService") {
    override fun onCreate() {
        super.onCreate()

    }

    override fun onHandleIntent(intent: Intent?) {
        notification(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, MyService::class.java))
        }
    }
}

class MyService : Service() {


    override fun onCreate() {
        super.onCreate()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}