package xyz.moshimoshi.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
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

        sharedPrefs = getSharedPreferences(getString(R.string.preference), MODE_PRIVATE)

        drawer = findViewById(R.id.drawer)

        val toolbar: Toolbar = initToolbar(R.id.toolbar)
        val nav: NavigationView = findViewById(R.id.nav)

        initToolbar(R.id.toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, 0,0)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        nav.setNavigationItemSelectedListener(this)

        nav.setCheckedItem(R.id.home)

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
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}