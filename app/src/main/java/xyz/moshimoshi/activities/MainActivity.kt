package xyz.moshimoshi.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.R
import xyz.moshimoshi.fragments.HomeFragment

class MainActivity: BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawer: DrawerLayout
    lateinit var sharedPrefs: SharedPreferences

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

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun newMessagePressed(view: View){
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
    }
}