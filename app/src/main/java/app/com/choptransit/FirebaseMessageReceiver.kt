package app.com.choptransit

import android.app.Notification.DEFAULT_VIBRATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageReceiver : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        showNotification(
            remoteMessage.data["title"],
            remoteMessage.data["message"]
        )
    }

    private fun getCustomDesign(
        title: String?,
        message: String?
    ): RemoteViews {
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.notification_layout
        )
        remoteViews.setTextViewText(R.id.tvTitle, title)
        remoteViews.setTextViewText(R.id.tvDesc, message)
        remoteViews.setImageViewResource(
            R.id.ivLogo,
            R.drawable.applogo
        )
        return remoteViews
    }

    fun showNotification(
        title: String?,
        message: String?
    ) {
        val intent = Intent(this, SplashActivity::class.java)
        val channel_id = "notification_channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        var builder = NotificationCompat.Builder(
            applicationContext,
            channel_id
        )
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        builder = builder.setContent(
            getCustomDesign(title, message)
        )
        val notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channel_id, getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
        notificationManager.notify(0, builder.build())
        if (title.toString().trim().equals("Booking Request Received", ignoreCase = true)) {
            newRideNotificationSound(this)
            newRideNotificationSound(this)
            newRideNotificationSound(this)
            setVibrate(this)
        }
    }

    fun newRideNotificationSound(context: Context) {
        try {
            val mediaPlayer = MediaPlayer()
            val afd = context.assets.openFd("notification.mp3")
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.localizedMessage
        }
    }

    fun setVibrate(mContext: Context) {
        try {
            val vib = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(
                    VibrationEffect.createOneShot(
                        DEFAULT_VIBRATE.toLong(),
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vib.vibrate(DEFAULT_VIBRATE.toLong())
            }
        } catch (e: Exception) {
        }
    }
}