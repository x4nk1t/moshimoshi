package xyz.moshimoshi.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.R
import xyz.moshimoshi.fragments.HomeFragment

class MainActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = Firebase.auth.currentUser

        if(user == null){
            Toast.makeText(this, "Something went wrong! Please login again!", Toast.LENGTH_SHORT).show()
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
            return
        }

        initToolbar(R.id.toolbar)
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.main_fragment, HomeFragment())
                transaction.commit()
            }
            R.id.logout -> {
                Firebase.auth.signOut()
                Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show()
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}