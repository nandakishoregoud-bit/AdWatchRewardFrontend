package com.example.myandroidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myandroidapp.models.QuestionDTOForPost
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostQuestionActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnPostQuestion: Button
    private var userId: Long = -1 // Default value if userId is not found

    // List of categories for the dropdown
    private val categories = listOf("All", "Technology", "Science", "Health", "Sports", "Entertainment")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_question)



        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)

            // Enable back button in ActionBar
            setDisplayHomeAsUpEnabled(true)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "Post Question"
        }

        // Retrieve userId from SharedPreferences
        userId = getUserIdFromSharedPreferences()
        Log.d("QuestionDetailsActivity", "Retrieved userID ID: $userId")

        // Initialize the views
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnPostQuestion = findViewById(R.id.btnPostQuestion)

        // Populate the Spinner with categories
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Handle Post Question Button Click
        btnPostQuestion.setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            val category = spinnerCategory.selectedItem.toString()

            if (title.isNotBlank() && description.isNotBlank() && category.isNotBlank()) {
                // Call postQuestion to post the new question
                postQuestion(title, description, category, userId)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to post a question using Retrofit
    private fun postQuestion(title: String, description: String, category: String, userId: Long) {
        val questionDTOForPost = QuestionDTOForPost(title, description, category, userId)

        RetrofitClient.questionApiService.postQuestion(userId, questionDTOForPost)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    when {
                        response.isSuccessful -> {
                            // Question posted successfully
                            Toast.makeText(this@PostQuestionActivity, "Question posted successfully", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity
                        }
                        response.code() == 403 -> {
                            // User is blocked
                            Toast.makeText(this@PostQuestionActivity, "You are blocked from posting questions.", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            // General failure message
                            Toast.makeText(this@PostQuestionActivity, "Failed to post question", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@PostQuestionActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    // Function to retrieve userId from SharedPreferences
    private fun getUserIdFromSharedPreferences(): Long {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("USER_ID", -1) // Default to -1 if not found
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
