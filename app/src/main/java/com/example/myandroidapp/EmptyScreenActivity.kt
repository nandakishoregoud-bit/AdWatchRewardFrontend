package com.example.myandroidapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds

class EmptyScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_screen)

        // Initialize AdMob
        MobileAds.initialize(this) {}

        /*// Set ActionBar title (optional)
        supportActionBar?.title = "Empty Screen"*/

        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "Ads Community"
        }
    }

    // Create menu with 3-line (hamburger) icon
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // Load menu file
        return true
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {  // Handle back button
                finish() // Closes RegisterActivity
                true
            }
            R.id.menu_welcome -> { // Handle menu icon click
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
