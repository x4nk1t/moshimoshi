package xyz.moshimoshi.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import xyz.moshimoshi.R

class LoginActivity : BaseActivity() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPrefs = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
    }

    fun buttonPressed(view: View) {
        val tokenBox: EditText = findViewById(R.id.input_token)
        val token = tokenBox.text.toString()

        if (token == "") {
            Toast.makeText(this, "Please enter your token!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Authenticating", Toast.LENGTH_SHORT).show()
        }
    }
}