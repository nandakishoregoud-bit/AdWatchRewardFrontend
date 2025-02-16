package com.example.myandroidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.adapters.QuestionAdapter
import com.example.myandroidapp.models.QuestionDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var btnPostQuestion: Button

    private lateinit var adapter: QuestionAdapter
    private val questionsList = mutableListOf<QuestionDTO>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        val etSearch = findViewById<EditText>(R.id.et_search)



        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)

            // Enable back button in ActionBar
            setDisplayHomeAsUpEnabled(true)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "Community"
        }

        recyclerView = findViewById(R.id.recycler_view_questions)
        spinner = findViewById(R.id.spinner_category)
        btnPostQuestion = findViewById(R.id.btn_post_question) // Reference the button

        // Retrieve USER_ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)

        // ❌ Do NOT return if user is not logged in (show questions anyway)
        if (userId == -1L) {
            Toast.makeText(this, "You are viewing as a guest!", Toast.LENGTH_SHORT).show()
            btnPostQuestion.isEnabled = false // Disable posting if not logged in
            btnPostQuestion.alpha = 0.5f // Make button look inactive
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString()) // ✅ Filter list with each word typed
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = QuestionAdapter(
            questionsList,
            userId

        )
        recyclerView.adapter = adapter

        // Setup Spinner
        val categories = listOf("All","Technology", "Science", "Health", "Sports", "Entertainment") // Define categories
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // Fetch all questions initially
        fetchQuestions(null)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = if (position == 0) null else categories[position]
                fetchQuestions(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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



    private fun fetchQuestions(category: String?) {
        RetrofitClient.questionApiService.getQuestions(category).enqueue(object : Callback<List<QuestionDTO>> {
            override fun onResponse(call: Call<List<QuestionDTO>>, response: Response<List<QuestionDTO>>) {
                if (response.isSuccessful) {
                    questionsList.clear()
                    response.body()?.let {
                        questionsList.addAll(it)

                        // ✅ Update the original list for filtering
                        adapter.updateOriginalList(it)
                    }
                    adapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(this@QuestionsActivity, "Failed to fetch questions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<QuestionDTO>>, t: Throwable) {
                Toast.makeText(this@QuestionsActivity, "Failed to fetch questions", Toast.LENGTH_SHORT).show()
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
