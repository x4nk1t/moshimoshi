package xyz.moshimoshi.activities

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.R
import xyz.moshimoshi.adapters.MessageAdapter
import xyz.moshimoshi.models.Message
import xyz.moshimoshi.utils.ChatFunctions.Companion.createChatBox
import xyz.moshimoshi.utils.ChatFunctions.Companion.getUsernameFromId
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageActivity: BaseActivity() {
    private var chatId: String? = null
    private var senderId: String? = null
    private var receiverId: String? = null
    private var allMessages: ArrayList<Message> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        initToolbar(R.id.messageToolbar)

        senderId = Firebase.auth.currentUser!!.uid
        chatId = intent.getStringExtra("chatId")
        receiverId = intent.getStringExtra("receiverId")

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(chatId.hashCode())

        val messageInput: EditText = findViewById(R.id.messageInput)
        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(findViewById(R.id.messageConstraintLayout))
                return@setOnEditorActionListener true
            }
            false
        }

        recyclerView = findViewById(R.id.messageRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.stackFromEnd = true

        adapter = MessageAdapter(this, allMessages, senderId.toString())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        getUsernameFromId(receiverId!!){ username ->
            supportActionBar?.title = username
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            if(chatId != null) {
                registerMessageListener()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun registerMessageListener(){
        val database = Firebase.firestore
        database.collection("messages").whereEqualTo("chats_id", chatId)
            .addSnapshotListener { snapshots, e ->
                if(e != null){
                    Log.e("MessageListener", "Failed to listen!")
                    return@addSnapshotListener
                }
                if(snapshots != null){
                    val receivedDocuments = snapshots.documentChanges
                    receivedDocuments.forEach { datas ->
                        if(datas.type == DocumentChange.Type.ADDED) {
                            val messageId = datas.document.id
                            val receivedData = datas.document.data
                            val receiverId = receivedData["receiverId"] as String
                            val senderId = receivedData["senderId"] as String
                            val timestamp = receivedData["timestamp"] as Long
                            val readTimestamp = receivedData["readTimestamp"] as Long
                            val message = receivedData["message"] as String

                            allMessages.add(Message(messageId, chatId, senderId, receiverId, message, timestamp, readTimestamp))
                            allMessages.sortBy { m -> m.timestamp }

                            adapter.notifyItemInserted(allMessages.size - 1)
                            recyclerView.scrollToPosition(allMessages.size - 1)
                        }
                    }
                }
            }
    }

    fun sendMessage(view: View){
        val database = Firebase.firestore
        val messageInputView: EditText = findViewById(R.id.messageInput)

        messageInputView.text.clear()
        if(messageInputView.text.toString() != ""){
            val messageModel = Message(null, chatId, senderId, receiverId, messageInputView.text.toString())

            createChatboxIfNotAvailable(receiverId!!){ _ ->
                database.collection("messages").add(messageModel.toHash())
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            messageModel.id = it.result.id
                            changeLastMessage(messageInputView.text.toString())
                        }
                    }
            }
        }
    }

    private fun changeLastMessage(message: String){
        val database = Firebase.firestore
        val hashMap = HashMap<String, Any>()
        hashMap["active"] = true
        hashMap["lastMessage"] = message
        hashMap["lastMessageBy"] = senderId!!
        hashMap["lastMessageTimestamp"] = System.currentTimeMillis()

        database.collection("chats").document(chatId!!).set(hashMap)
    }

    private fun createChatboxIfNotAvailable(userId: String, callback: (found: Boolean) -> Unit){
        val database = Firebase.firestore

        database.collection("chatbox").document(userId).get()
            .addOnCompleteListener {
                if(it.result.data == null){
                    createChatBox(userId, receiverId!!, chatId!!){ success ->
                        callback.invoke(success)
                    }
                } else {
                    val chats = it.result.data as Map<*, *>
                    chats.forEach { chatDetail ->
                        val chatUserId = chatDetail.key

                        if(chatUserId == senderId){
                            callback.invoke(true)
                        }
                    }
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}