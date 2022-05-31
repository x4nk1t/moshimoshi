package xyz.moshimoshi.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import xyz.moshimoshi.R

abstract class BaseActivity: AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("xyz.moshimoshi.chatState", Context.MODE_PRIVATE)
    }

    fun initToolbar(id: Int): Toolbar {
        val bar: Toolbar = findViewById(id)
        setSupportActionBar(bar)
        bar.setTitleTextColor(getColor(R.color.textColor))
        return bar
    }

    fun setForeground(set: String){
        val editor = sharedPreferences.edit()
        editor.putString("currentForeground", set)
        editor.apply()
    }
}