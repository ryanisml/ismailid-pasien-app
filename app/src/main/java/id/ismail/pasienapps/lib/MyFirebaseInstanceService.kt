package id.ismail.pasienapps.lib

import android.annotation.SuppressLint
import android.app.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.content.Intent
import id.ismail.pasienapps.MainActivity
import android.app.PendingIntent
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
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("statusid", statusid)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
        )
        val channelId = "id.ismail.pasienapps"
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(deskripsi))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Reservation Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        manager.notify(0, builder.build())
    }
}