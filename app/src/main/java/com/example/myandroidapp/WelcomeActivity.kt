package com.example.myandroidapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myandroidapp.network.RetrofitClient
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)



        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)

            // Enable back button in ActionBar
            setDisplayHomeAsUpEnabled(true)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "welcome"
        }

        // Initialize AdMob
        MobileAds.initialize(this) {}

        val welcomeMessage = findViewById<TextView>(R.id.welcome_message)

        // Retrieve user data from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "Guest")
        val userId = sharedPreferences.getLong("USER_ID", -1)

        // Display user data
        welcomeMessage.text = "Welcome $userName! Your ID is $userId"



        // Set up dynamic buttons for navigation to Ad-Watch Activity

        setUpButton(R.id.wallet_button, WalletActivity::class.java)
        setUpButton(R.id.profile_button, UserProfileActivity::class.java)
        setUpButton(R.id.popular_button, PopularQuestionsActivity::class.java)
        setUpButton(R.id.questions_button, QuestionsActivity::class.java)
        setUpButton(R.id.btn_user_questions, UserQuestionsActivity::class.java)
    }

    private fun setUpButton(buttonId: Int, targetActivityClass: Class<*>) {
        val button: Button = findViewById(buttonId)
        button.setOnClickListener {
            val intent = Intent(this, AdWatchActivity::class.java).apply {
                putExtra("TARGET_ACTIVITY_CLASS", targetActivityClass.name)
            }
            startActivity(intent)
        }
    }



    // Handle back button click
    override fun onSupportNavigateUp(): Boolean {
        finish() // Closes this activity and goes back
        return true
    }

    
}
