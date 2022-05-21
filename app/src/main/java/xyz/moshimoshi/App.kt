package xyz.moshimoshi

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp

class App: Application() {
    companion object{
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
    }
}