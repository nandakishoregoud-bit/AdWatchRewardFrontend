package com.example.myandroidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.adapters.UserQuestionAdapter
import com.example.myandroidapp.models.QuestionDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserQuestionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserQuestionAdapter
    private val questionsList = mutableListOf<QuestionDTO>()
    private lateinit var btnPostQuestion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_questions)



        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)

            // Enable back button in ActionBar
            setDisplayHomeAsUpEnabled(true)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "My Questions"
        }

        recyclerView = findViewById(R.id.recyclerViewUserQuestions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btnPostQuestion = findViewById(R.id.btn_post_question)

        // ✅ Initialize the adapter here
        adapter = UserQuestionAdapter(questionsList)
        recyclerView.adapter = adapter  // Set the adapter to RecyclerView

        val userId = getUserIdFromPreferences()
        if (userId != -1L) {
            fetchUserQuestions(userId)
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
        }

        setUpButton(R.id.btn_post_question, PostQuestionActivity::class.java)


    }

    private fun setUpButton(buttonId: Int, targetActivityClass: Class<*>) {
        val button: Button? = findViewById(buttonId) // Nullable to prevent crash
        if (button == null) {
            Log.e("QuestionsActivity", "Button with ID $buttonId not found!")
            return
        }

        button.setOnClickListener {
            Log.d("QuestionsActivity", "Button clicked: ${button.text}") // Debug log

            val intent = Intent(this, AdWatchActivity::class.java).apply {
                putExtra("TARGET_ACTIVITY_CLASS", targetActivityClass.name)
            }
            startActivity(intent)
        }
    }

    private fun getUserIdFromPreferences(): Long {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("USER_ID", -1)
    }

    private fun fetchUserQuestions(userId: Long) {
        RetrofitClient.questionApiService.getQuestionsByUser(userId).enqueue(object :
            Callback<List<QuestionDTO>> {
            override fun onResponse(call: Call<List<QuestionDTO>>, response: Response<List<QuestionDTO>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        adapter.updateData(it)  //  Update adapter using this method
                    }
                } else {
                    Toast.makeText(this@UserQuestionsActivity, "Failed to fetch user questions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<QuestionDTO>>, t: Throwable) {
                Toast.makeText(this@UserQuestionsActivity, "Failed to fetch user questions", Toast.LENGTH_SHORT).show()
            }
        })
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
