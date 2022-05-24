package xyz.moshimoshi.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.R
import xyz.moshimoshi.utils.ChatFunctions.Companion.createNewChat
import xyz.moshimoshi.utils.ChatFunctions.Companion.openChat

class NewMessageActivity: BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_message)
        initToolbar(R.id.newMessageToolbar)

        supportActionBar?.title = "New Conversation"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val searchView: SearchView = findViewById(R.id.search_username)
        val store = Firebase.firestore

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    val userData = store.collection("users").whereEqualTo("username", query).get()

                    userData.addOnCompleteListener {
                        if(it.isSuccessful){
                            val result = it.result
                            if(result.documents.size > 0) {
                                val userDetail = result.documents[0]
                                val username = userDetail.get("username").toString()
                                val userId = userDetail.id

                                checkChatAvailable(userId, username)
                            } else {
                                Toast.makeText(applicationContext, "User not found!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                return true
            }
        })
    }

    fun checkChatAvailable(userId: String, username: String){
        val database = Firebase.firestore
        val user = Firebase.auth
        val chatBoxes = database.collection("chatbox").document(user.uid!!).get()

        chatBoxes.addOnCompleteListener(this) {
            if(it.isSuccessful){
                if(it.result.data == null) {
                    createNewChat(userId, username)
                } else {
                    val result = it.result
                    val chatUsers = result.data?.get("chats") as Map<*, *>

                    if (chatUsers.isEmpty()) {
                        createNewChat(userId, username)
                    } else {
                        chatUsers.forEach { chatDetails ->
                            val chatUserId = chatDetails.key
                            val chatId = chatDetails.value as String

                            if(userId == chatUserId){
                                openChat(this, chatId, chatUserId)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}