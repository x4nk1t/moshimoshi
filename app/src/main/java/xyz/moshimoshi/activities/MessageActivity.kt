package xyz.moshimoshi.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import xyz.moshimoshi.R
import xyz.moshimoshi.models.Message
import xyz.moshimoshi.utils.ChatFunctions

class MessageActivity: BaseActivity() {
    private lateinit var allMessages: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        initToolbar(R.id.messageToolbar)

        val chatId = intent.getStringExtra("chatId")
        val username = intent.getStringExtra("username")

        supportActionBar?.title = username
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(chatId != null) {
            loadMessages(chatId){}
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadMessages(chatId: String, callback: (messageLoaded: Boolean) -> Unit){
        ChatFunctions.getMessages(chatId) { messages ->
            allMessages = messages
            callback.invoke(true)
            if (messages.size > 0) {
                Toast.makeText(this, "Messages count: " + messages.size, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Messages not found!", Toast.LENGTH_SHORT).show()
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