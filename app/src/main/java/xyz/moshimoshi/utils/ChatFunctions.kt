package xyz.moshimoshi.utils

import android.app.Activity
import android.content.Intent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.activities.MessageActivity
import xyz.moshimoshi.models.Message

class ChatFunctions {
    companion object {
        fun createNewChat(userId: String, username: String){
            //TODO
        }

        fun getUsernameFromId(id: String, callback: (username: String) -> Unit){
            val database = Firebase.firestore
            val users = database.collection("users").document(id).get()

            users.addOnCompleteListener {
                if(it.isSuccessful){
                    val result = it.result
                    if(result == null){
                        callback.invoke("")
                    } else {
                        callback.invoke(result.get("username").toString())
                    }
                }
            }
        }

        fun getMessages(chatId: String, callback: (messages: ArrayList<Message>) -> Unit){
            val messages = ArrayList<Message>()
            val database = Firebase.firestore
            val chats = database.collection("messages").whereEqualTo("chats_id", chatId).get()

            chats.addOnCompleteListener {
                if (it.isSuccessful){
                    if(it.result != null){
                        val documents = it.result.documents

                        documents.forEach { docs ->
                            val messageId = docs.id
                            val receiverId = docs.get("receiverId") as String
                            val senderId = docs.get("senderId") as String
                            val timestamp = docs.get("timestamp") as Long
                            val readTimestamp = docs.get("readTimestamp") as Long
                            val message = docs.get("message") as String

                            messages.add(Message(messageId, chatId, senderId, receiverId, message, timestamp, readTimestamp))

                            if(docs.id == it.result.documents.last().id){
                                callback.invoke(messages)
                            }
                        }

                        if(it.result.documents.size <= 0){
                            callback.invoke(ArrayList())
                        }
                    } else {
                        callback.invoke(ArrayList())
                    }
                }
            }
        }

        fun openChat(activity: Activity, chatId: String, receiverId: String){
            val intent = Intent(activity, MessageActivity::class.java)
            intent.putExtra("chatId", chatId)
            intent.putExtra("receiverId", receiverId)
            activity.startActivity(intent)
        }
    }
}