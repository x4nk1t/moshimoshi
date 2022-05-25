package xyz.moshimoshi.utils

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.activities.MessageActivity
import xyz.moshimoshi.models.Message

class ChatFunctions {
    companion object {
        fun createNewChat(activity: Activity, userId: String){
            val currentUser = Firebase.auth.currentUser!!

            if(userId == currentUser.uid){
                Toast.makeText(activity, "You cannot chat with yourself!", Toast.LENGTH_SHORT).show()
            } else {
                createChats { chatId ->
                    if(chatId != "") {
                        createChatBox(userId, currentUser.uid, chatId) { success ->
                            if (success) {
                                openChat(activity, chatId, userId)
                                activity.finish()
                            } else {
                                Toast.makeText(activity, "Something went wrong! Please try again in few minutes!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(activity, "Something went wrong! Please try again in few minutes!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun createChats(callback: (chatId: String) -> Unit){
            val database = Firebase.firestore

            val chatsHashMap = HashMap<String, Any>()
            chatsHashMap["active"] = true
            chatsHashMap["lastMessage"] = ""
            chatsHashMap["lastMessageBy"] = ""
            chatsHashMap["lastMessageTimestamp"] = System.currentTimeMillis()

            database.collection("chats").add(chatsHashMap)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        callback.invoke(it.result.id)
                    } else {
                        callback.invoke("")
                    }
                }
        }

        private fun getUsersChatBox(userId: String, callback: (chatboxes: HashMap<String, Any>) -> Unit){
            val database = Firebase.firestore

            database.collection("chatbox").document(userId).get()
                .addOnCompleteListener { boxes ->
                    if(boxes.isSuccessful){
                        if(boxes.result.data != null){
                            val chats = boxes.result.data as HashMap<String, Any>
                            callback.invoke(chats)
                        } else {
                            callback.invoke(HashMap())
                        }
                    }
                }
        }

        fun createChatBox(senderId: String, receiverId: String, chatId: String, callback: (success: Boolean) -> Unit){
            val database = Firebase.firestore

            getUsersChatBox(senderId) { hashMap ->
                hashMap[receiverId] = chatId

                database.collection("chatbox").document(senderId).set(hashMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            getUsersChatBox(receiverId) { hashMap2 ->
                                hashMap2[senderId] = chatId

                                database.collection("chatbox").document(receiverId).set(hashMap2)
                                    .addOnCompleteListener { it2 ->
                                        if(it2.isSuccessful){
                                            callback.invoke(true)
                                        } else {
                                            callback.invoke(false)
                                        }
                                    }
                            }
                        } else {
                            callback.invoke(false)
                        }
                    }
            }
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
                                messages.sortBy { singleMessage -> singleMessage.timestamp }
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