package xyz.moshimoshi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
        val loginStatus: TextView = findViewById(R.id.login_status)

        if(email == "" || password == ""){
            loginStatus.setTextColor(getColor(R.color.red))
            loginStatus.text = getString(R.string.enter_both)
            return
        }

        loginStatus.setTextColor(getColor(R.color.black))
        loginStatus.text = getString(R.string.logging_in)

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
                    val loginStatus: TextView = findViewById(R.id.login_status)

                    loginStatus.text = getString(R.string.user_not_found)
                    loginStatus.setTextColor(getColor(R.color.red))
                }
            }
    }
}