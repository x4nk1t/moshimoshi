package xyz.moshimoshi.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.models.Message

class ChatFunctions {
    companion object {
        fun openNewChat(userId: String, username: String){
            //TODO
        }

        fun getMessages(chatId: String, callback: (messages: ArrayList<Message>) -> Unit){
            val messages = ArrayList<Message>()
            val database = Firebase.firestore
            val chats = database.collection("messages").whereEqualTo("chats_id", chatId).get()

            chats.addOnCompleteListener {
                if (it.isSuccessful){
                    if(it.result != null){
                        it.result.documents.forEach { docs ->
                            val messageId = docs.id
                            val chatId = docs.get("chats_id") as String
                            val receiverId = docs.get("receiverId") as String
                            val senderId = docs.get("senderId") as String
                            val timestamp = docs.get("timestamp") as String
                            val readTimestamp = docs.get("readTimestamp") as String
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
                        Log.e("chatbox", "result not found")
                        callback.invoke(ArrayList())
                    }
                }
            }
        }

        fun openChat(context: Context, chatId: String){
            getMessages(chatId){ messages ->
                if(messages.size > 0){
                    //TODO: Open new activity and display all messages
                    Toast.makeText(context, "Messages count: "+ messages.size, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Messages not found!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}