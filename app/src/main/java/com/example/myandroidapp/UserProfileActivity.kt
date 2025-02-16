package com.example.myandroidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myandroidapp.models.UserProfileDTO
import com.example.myandroidapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var coinsTextView: TextView
    private lateinit var adsWatchedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)



        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)

            // Enable back button in ActionBar
            setDisplayHomeAsUpEnabled(true)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "profile"
        }

        // Initialize UI components
        nameTextView = findViewById(R.id.nameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        coinsTextView = findViewById(R.id.coinsTextView)
        adsWatchedTextView = findViewById(R.id.adsWatchedTextView)

        // Fetch user ID from shared preferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)

        // Fetch user profile data
        if (userId != -1L) {
            fetchUserProfile(userId)
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserProfile(userId: Long) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitClient.userApiService.getUserProfile(userId)
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    displayUserProfile(userProfile)
                } else {
                    Toast.makeText(this@UserProfileActivity, "Failed to fetch user profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UserProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayUserProfile(userProfile: UserProfileDTO?) {
        userProfile?.let {
            nameTextView.text = "Name: ${it.name}"
            emailTextView.text = "Email: ${it.email}"
            coinsTextView.text = "Coins: ${"%.4f".format(it.coins)}"
            adsWatchedTextView.text = "Ads Watched: ${it.adsWatched}"
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

    // Handle back button click
    override fun onSupportNavigateUp(): Boolean {
        finish() // Closes this activity and goes back
        return true
    }
}
