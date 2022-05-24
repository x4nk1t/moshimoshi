package xyz.moshimoshi.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.R
import xyz.moshimoshi.adapters.MessageAdapter
import xyz.moshimoshi.models.Message
import xyz.moshimoshi.utils.ChatFunctions
import xyz.moshimoshi.utils.ChatFunctions.Companion.getUsernameFromId

class MessageActivity: BaseActivity() {
    private var chatId: String? = null
    private var senderId: String? = null
    private var receiverId: String? = null
    private var allMessages: ArrayList<Message> = ArrayList()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        initToolbar(R.id.messageToolbar)

        recyclerView = findViewById(R.id.messageRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        senderId = Firebase.auth.currentUser!!.uid

        chatId = intent.getStringExtra("chatId")
        receiverId = intent.getStringExtra("receiverId")

        getUsernameFromId(receiverId!!){ username ->
            supportActionBar?.title = username
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            if(chatId != null) {
                loadMessages(chatId!!){
                    allMessages = it
                    recyclerView.adapter = MessageAdapter(this, allMessages, senderId.toString())
                    recyclerView.layoutManager = layoutManager
                }
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun loadMessages(chatId: String, callback: (messageLoaded: ArrayList<Message>) -> Unit){
        ChatFunctions.getMessages(chatId) { messages ->
            if (messages.size == 0) {
                Toast.makeText(this, "No messages found!", Toast.LENGTH_SHORT).show()
            }
            callback.invoke(messages)
        }
    }

    fun sendMessage(view: View){
        val database = Firebase.firestore
        val messageInputView: EditText = findViewById(R.id.messageInput)

        if(messageInputView.text.toString() != ""){
            val messageModel = Message(null, chatId, senderId, receiverId, messageInputView.text.toString())

            createChatboxIfNotAvailable(receiverId!!){ created ->
                database.collection("messages").document().set(messageModel.toHash())
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            allMessages.add(messageModel)
                            recyclerView.adapter!!.notifyDataSetChanged()
                        }
                    }
            }
        }
    }

    private fun createChatboxIfNotAvailable(userId: String, callback: (found: Boolean) -> Unit){
        val database = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        database.collection("chatbox").document(userId).get()
            .addOnCompleteListener {
                val chatHashMap = HashMap<String, String>()
                chatHashMap[currentUser.uid] = chatId!!

                if(it.result.data == null){
                    val chats = HashMap<String, Any>()
                    chats["chats"] = chatHashMap

                    database.collection("chatbox").document(userId).set(chats)
                    callback.invoke(true)
                } else {
                    val chats = it.result.data!!["chats"] as Map<*, *>
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