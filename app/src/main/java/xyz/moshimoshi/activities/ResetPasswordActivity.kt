package xyz.moshimoshi.activities

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.R

class ResetPasswordActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val emailInput: EditText = findViewById(R.id.resetEmailInput)
        emailInput.setOnEditorActionListener { _, actionCode, _ ->
            if(actionCode == EditorInfo.IME_ACTION_SEND){
                resetPassword(findViewById(R.id.resetPasswordConstraintLayout))
                return@setOnEditorActionListener true
            }
            false
        }
    }

    fun resetPassword(view: View){
        val auth = Firebase.auth
        val emailInput: EditText = findViewById(R.id.resetEmailInput)
        val email = emailInput.text.toString()

        if(email == ""){
            sendToast("Please enter your email!")
        } else {
            emailInput.text.clear()
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        sendToast("Password reset sent successfully! Check your email!")
                    } else {
                        sendToast("Something went wrong! Please try again in few minutes!")
                    }
                    finish()
                }
        }
    }

    private fun sendToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}