package xyz.moshimoshi

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import xyz.moshimoshi.utils.ChatFunctions
import xyz.moshimoshi.utils.NotificationReceiver

private const val channelId = "notification_channel"
private const val channelName = "Receive Messages"
private const val channelDescription = "Shows notifications whenever work starts"

class FirebaseService: FirebaseMessagingService() {
    private val resultKey = "reply_message_key"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        if(data.isEmpty()){
            return
        }

        val receivedMessage = data["message"]!!
        val senderId = data["senderId"]!!
        val chatId = data["chatId"]!!

        ChatFunctions.getUsernameFromId(senderId){ senderUsername ->
            generateNotification(chatId, senderId, senderUsername, receivedMessage)
        }
    }

    private fun generateNotification(chatId: String, senderId: String, senderUsername: String, message: String){
        val resultIntent = Intent(this, NotificationReceiver::class.java)
        resultIntent.putExtra("chatId", chatId)
        resultIntent.putExtra("receiverId", senderId)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val chatIdInt = chatId.hashCode()
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, resultIntent, PendingIntent.FLAG_MUTABLE)

        val remoteInput = RemoteInput.Builder(resultKey).build()
        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground, "Reply", pendingIntent)
            .addRemoteInput(remoteInput).build()

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon((getBitmapDrawable()))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContentTitle(senderUsername)
            .setContentText(message)
            .addAction(replyAction)

        notificationManager().activeNotifications.forEach { statusBarNotification ->
            if(statusBarNotification.id == chatId.hashCode()){
                val extras = statusBarNotification.notification.extras

                val previousText = extras.get("android.text") as String
                val newContent = "$previousText \n$message"

                builder.setContentText(newContent)
            }
        }

        createNotificationChannel()
        notificationManager().notify(chatIdInt, builder.build())
    }

    private fun notificationManager(): NotificationManager {
        return applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun getBitmapDrawable(): Bitmap? {
        return AppCompatResources.getDrawable(this, R.drawable.ic_launcher_foreground)
            ?.toBitmap()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName
            val description = channelDescription
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            notificationManager().createNotificationChannel(channel)
        }
    }
}