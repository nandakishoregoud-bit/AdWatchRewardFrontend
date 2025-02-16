package com.example.myandroidapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.adapters.CommentAdapter
import com.example.myandroidapp.models.Comment
import com.example.myandroidapp.models.CommentDTO
import com.example.myandroidapp.models.FlagRequest
import com.example.myandroidapp.models.QuestionDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class QuestionDetailsActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvLikes: TextView
    private lateinit var tvdislikes: TextView
    private lateinit var tvUpdatedAt: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter
    private val commentList = mutableListOf<CommentDTO>()
    private lateinit var etCommentInput: EditText
    private lateinit var btnPostComment: Button
    private var questionId: Long = -1
    private var userId: Long = -1

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)



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

        // Initialize views
        tvTitle = findViewById(R.id.tv_question_title)
        tvDescription = findViewById(R.id.tv_question_description)
        tvLikes = findViewById(R.id.tv_question_likes)
        tvdislikes = findViewById(R.id.tv_question_dislikes)
        tvUpdatedAt = findViewById(R.id.tv_question_updated_at)
        recyclerView = findViewById(R.id.recycler_view_comments)
        etCommentInput = findViewById(R.id.et_comment_input)
        btnPostComment = findViewById(R.id.btn_post_comment)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CommentAdapter(commentList,
            onDeleteComment = { commentId ->
                deleteComment(commentId)
            },
            onFlagComment = { commentId, reason -> flagComment(commentId, reason.toString())}


                )
        recyclerView.adapter = adapter

        // Get question ID from intent
        questionId = intent.getLongExtra("questionId", -1) // Assign to class-level variable

        // Log the retrieved question ID
        Log.d("QuestionDetailsActivity", "Retrieved Question ID: $questionId")

        // Retrieve the userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("USER_ID", -1)

        if (questionId != -1L) {
            fetchQuestionDetails(questionId)
        } else {
            Toast.makeText(this, "Invalid question ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        tvLikes.setOnClickListener {
            likeQuestion()
        }

        tvdislikes.setOnClickListener {
            dislikeQuestion()
        }

        // Handle posting a new comment
        btnPostComment.setOnClickListener {
            val commentText = etCommentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                postComment(commentText)
            } else {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatDate(dateString: String?): String {
        return if (!dateString.isNullOrEmpty()) {
            try {
                val parsedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateString)
                val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(parsedDate!!)
                "Posted At: $formattedDate"
            } catch (e: Exception) {
                "Posted At: --"
            }
        } else {
            "Posted At: --"
        }
    }

    private fun fetchQuestionDetails(questionId: Long) {
        RetrofitClient.questionApiService.getQuestionDetails(questionId).enqueue(object :
            Callback<QuestionDTO> {
            override fun onResponse(call: Call<QuestionDTO>, response: Response<QuestionDTO>) {
                if (response.isSuccessful) {
                    response.body()?.let { question ->
                        tvTitle.text = question.title
                        tvDescription.text = question.description
                        tvLikes.text = "Likes: ${question.likes}"
                        tvdislikes.text = "Dislikes: ${question.dislikes}"
                        tvUpdatedAt.text = formatDate(question.createdAt)

                        commentList.clear()
                        question.comments?.let { commentList.addAll(it) }
                        adapter.notifyDataSetChanged()

                        // Scroll to the bottom of the RecyclerView
                        recyclerView.scrollToPosition(commentList.size - 1)
                    }
                } else {
                    Toast.makeText(this@QuestionDetailsActivity, "Failed to load question", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<QuestionDTO>, t: Throwable) {
                Toast.makeText(this@QuestionDetailsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun postComment(commentText: String) {
        val comment = Comment(text = commentText)
        Log.d("QuestionDetailsActivity", "Comment Text: $commentText")
        Log.d("QuestionDetailsActivity", "Question ID: $questionId")
        Log.d("QuestionDetailsActivity", "User ID: $userId")

        // Clear the input field and hide the keyboard immediately
        etCommentInput.text.clear()
        etCommentInput.clearFocus()
        hideKeyboard()

        // Make the API call to post the comment
        RetrofitClient.commentApiService.postComment(questionId, userId, comment)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("PostCommentResponse", "Response Code: ${response.code()}")
                    Log.d("PostCommentResponse", "Response Body: ${response.body()}")

                    when {
                        response.isSuccessful -> {
                            Toast.makeText(
                                this@QuestionDetailsActivity,
                                "Comment submitted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            reloadScreen()
                        }
                        response.code() == 403 -> {
                            // User is blocked from commenting
                            Toast.makeText(
                                this@QuestionDetailsActivity,
                                "You are blocked from posting comments.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        response.code() == 400 -> {
                            // Duplicate comment message
                            Toast.makeText(
                                this@QuestionDetailsActivity,
                                "You have already posted this comment.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            // General error
                            Toast.makeText(
                                this@QuestionDetailsActivity,
                                "Failed to post comment. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("PostCommentError", "Error: ${t.message}")
                    Toast.makeText(
                        this@QuestionDetailsActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun likeQuestion() {
        if (questionId == -1L || userId == -1L) return

        RetrofitClient.questionApiService.likeQuestion(questionId, userId).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val message = response.body() ?: "Action completed"
                    Toast.makeText(this@QuestionDetailsActivity, message, Toast.LENGTH_SHORT).show()

                    // Update like count
                    if (message.contains("removed")) {
                        tvLikes.text = "Likes: ${(tvLikes.text.split(": ")[1].toInt()) - 1}"
                    } else {
                        tvLikes.text = "Likes: ${(tvLikes.text.split(": ")[1].toInt()) + 1}"
                    }
                } else {
                    Toast.makeText(this@QuestionDetailsActivity, "Failed to update like", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@QuestionDetailsActivity, "Network Error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun dislikeQuestion() {
        if (questionId == -1L || userId == -1L) return

        RetrofitClient.questionApiService.dislikeQuestion(questionId, userId).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val message = response.body() ?: "Action completed"
                    Toast.makeText(this@QuestionDetailsActivity, message, Toast.LENGTH_SHORT).show()

                    // Update dislike count
                    if (message.contains("removed")) {
                        tvdislikes.text = "Dislikes: ${(tvdislikes.text.split(": ")[1].toInt()) - 1}"
                    } else {
                        tvdislikes.text = "Dislikes: ${(tvdislikes.text.split(": ")[1].toInt()) + 1}"
                    }
                } else {
                    Toast.makeText(this@QuestionDetailsActivity, "Failed to update dislike", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@QuestionDetailsActivity, "Network Error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // API Call to flag a comment
    private fun flagComment(commentId: Long, reason: String) {
        val flagRequest = FlagRequest(reason)

        RetrofitClient.commentApiService.flagComment(questionId,commentId,userId, flagRequest).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@QuestionDetailsActivity, "Comment flagged", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@QuestionDetailsActivity, "Failed to flag comment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@QuestionDetailsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun deleteComment(commentId: Long) {
        RetrofitClient.commentApiService.deleteComment(questionId, userId, commentId)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@QuestionDetailsActivity, "Comment deleted", Toast.LENGTH_SHORT).show()
                        fetchQuestionDetails(questionId) // Refresh comments
                    } else {
                        Toast.makeText(this@QuestionDetailsActivity, "Failed to delete comment", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@QuestionDetailsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Function to reload the current screen
    private fun reloadScreen() {
        val intent = intent // Get the current intent
        finish() // Close the current activity
        startActivity(intent) // Restart the activity
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(etCommentInput.windowToken, 0)
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
