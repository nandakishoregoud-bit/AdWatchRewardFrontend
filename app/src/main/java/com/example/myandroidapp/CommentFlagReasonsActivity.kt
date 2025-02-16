package com.example.myandroidapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.adapters.CommentFlagReasonAdapter
import com.example.myandroidapp.models.FlagReasonDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentFlagReasonsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentFlagReasonAdapter
    private var flagReasonsList = mutableListOf<FlagReasonDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_flag_reasons)

        recyclerView = findViewById(R.id.recyclerViewCommentFlagReasons)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val commentId = intent.getLongExtra("COMMENT_ID", -1L)
        if (commentId != -1L) {
            fetchFlagReasons(commentId)
        } else {
            Toast.makeText(this, "Invalid comment ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchFlagReasons(commentId: Long) {
        RetrofitClient.adminApiService.getFlagReasonsByCommentId(commentId).enqueue(object :
            Callback<List<FlagReasonDTO>> {
            override fun onResponse(
                call: Call<List<FlagReasonDTO>>,
                response: Response<List<FlagReasonDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        flagReasonsList.clear()
                        flagReasonsList.addAll(it)
                        adapter = CommentFlagReasonAdapter(flagReasonsList)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@CommentFlagReasonsActivity, "Failed to load flag reasons", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FlagReasonDTO>>, t: Throwable) {
                Toast.makeText(this@CommentFlagReasonsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
