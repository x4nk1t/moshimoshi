package xyz.moshimoshi.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class BaseActivity: AppCompatActivity() {

    fun initToolbar(id: Int): Toolbar {
        val bar: Toolbar = findViewById(id)
        setSupportActionBar(bar)
        return bar
    }
}