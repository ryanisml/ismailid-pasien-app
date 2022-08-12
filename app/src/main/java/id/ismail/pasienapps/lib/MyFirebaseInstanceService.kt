package id.ismail.pasienapps.lib

import android.annotation.SuppressLint
import android.app.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.content.Intent
import id.ismail.pasienapps.MainActivity
import android.app.PendingIntent
import android.os.Build
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import id.ismail.pasienapps.R
import android.graphics.BitmapFactory
import android.graphics.Color

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseInstanceService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            val body = data["body"]
            val deskripsi = data["deskripsi"]
            val statusid = data["statusid"]
            val title = data["title"]
            if (statusid != null) {
                showNotification(title, body, deskripsi, statusid.toInt())
            }
        }
    }

    private fun showNotification(title: String?, body: String?, deskripsi: String?, statusid: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        @SuppressLint("UnspecifiedImmutableFlag") val pendingIntent = PendingIntent.getActivity(
            this, statusid /* Request code */, notificationIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notifId = "id.ismail.pasienapps"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notifId, deskripsi,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = body
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, notifId)
            .setSmallIcon(R.drawable.patient_icon)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.patient_icon
                )
            )
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentInfo(deskripsi)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
        notificationManager.notify(statusid /* ID of notification */, notificationBuilder.build())
    }
}