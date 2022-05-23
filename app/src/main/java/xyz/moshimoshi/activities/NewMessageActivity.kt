package xyz.moshimoshi.activities

import android.os.Bundle
import android.view.MenuItem
import xyz.moshimoshi.R

class NewMessageActivity: BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_message)

        initToolbar(R.id.newMessageToolbar)

        supportActionBar?.title = "New Conversation"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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