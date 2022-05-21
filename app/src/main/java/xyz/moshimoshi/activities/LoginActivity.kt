package xyz.moshimoshi.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.R

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginPressed(view: View){
        val emailBox: EditText = findViewById(R.id.input_email)
        val passwordBox: EditText = findViewById(R.id.input_password)

        val email = emailBox.text.toString()
        val password = passwordBox.text.toString()

        if(email == "" || password == ""){
            Toast.makeText(this, "Please enter both email and password!", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Logging in..", Toast.LENGTH_SHORT).show()

        authenticateUser(email, password)
    }

    private fun authenticateUser(email: String, password: String){
        val instance = Firebase.auth

        instance.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show()

                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                } else {
                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}