package com.example.myandroidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myandroidapp.activities.BlockedUsersActivity
import com.google.android.gms.ads.MobileAds

class AdminDashboardActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard) // Using new XML file

        // Initialize AdMob
        MobileAds.initialize(this) {}

        // Button to navigate to AdminActivity
        val btnManageUsers = findViewById<Button>(R.id.btn_manage_users)
        btnManageUsers.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

        val btnFlaggedQuestions = findViewById<Button>(R.id.btn_flagged_question)
        btnFlaggedQuestions.setOnClickListener {
            val intent = Intent(this, FlaggedQuestionsActivity::class.java)
            startActivity(intent)
        }

        val btnFlaggedComments = findViewById<Button>(R.id.btn_flagged_comment)
        btnFlaggedComments.setOnClickListener {
            val intent = Intent(this, FlaggedCommentsActivity::class.java)
            startActivity(intent)
        }

        val btnBlockedUsers: Button = findViewById(R.id.btnBlockedUsers)
        btnBlockedUsers.setOnClickListener {
            val intent = Intent(this, BlockedUsersActivity::class.java)
            startActivity(intent)
        }

        val btnquestionsbutton: Button = findViewById(R.id.btnquestionsbutton)
        btnquestionsbutton.setOnClickListener {
            val intent = Intent(this, QuestionsActivity::class.java)
            startActivity(intent)
        }

        val btnpopularquestionsbutton: Button = findViewById(R.id.btnpopularquestionsbutton)
        btnpopularquestionsbutton.setOnClickListener {
            val intent = Intent(this, PopularQuestionsActivity::class.java)
            startActivity(intent)
        }
    }
}
