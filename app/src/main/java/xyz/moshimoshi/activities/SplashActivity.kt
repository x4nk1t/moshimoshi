package xyz.moshimoshi.activities

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import xyz.moshimoshi.R

class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val thread = Thread {
            Thread.sleep(2000)

            val afterIntent: Intent

            if (FirebaseAuth.getInstance().currentUser != null) {
                afterIntent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                afterIntent = Intent(this, LoginActivity::class.java)
            }
            startActivity(afterIntent)
            finish()
        }

        thread.start()
    }
}