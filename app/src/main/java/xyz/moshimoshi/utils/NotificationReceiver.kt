package xyz.moshimoshi.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.RemoteInput
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.activities.MessageActivity

class NotificationReceiver: BroadcastReceiver(){
    private val resultKey = "reply_message_key"

    override fun onReceive(context: Context?, intent: Intent?) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val chatId = intent!!.getStringExtra("chatId")
        val receiverId = intent.getStringExtra("receiverId")

        if (remoteInput != null) {
            Toast.makeText(context, "Sending reply!", Toast.LENGTH_SHORT).show()
            val inputMessage = remoteInput.getCharSequence(resultKey).toString()
            sendMessage(chatId.toString(), receiverId.toString(), inputMessage){
                if(it){
                    Toast.makeText(context, "Reply sent!", Toast.LENGTH_SHORT).show()
                    notificationManager(context!!.applicationContext).cancel(chatId.hashCode())
                }
            }
        }
    }

    private fun sendMessage(chatId: String, receiverId: String, message: String, callback: (success: Boolean) -> Unit){
        val database = Firebase.firestore
        val user = Firebase.auth.currentUser!!

        val messageModel = xyz.moshimoshi.models.Message(null, chatId, user.uid, receiverId, message)

        database.collection("messages").add(messageModel.toHash())
            .addOnCompleteListener {
                if (it.isSuccessful){
                    messageModel.id = it.result.id
                    ChatFunctions.changeLastMessage(chatId, user.uid, message)
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }
    }

    private fun notificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}