package com.example.myandroidapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.adapters.FlaggedCommentsAdapter
import com.example.myandroidapp.models.FlaggedCommentDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlaggedCommentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlaggedCommentsAdapter
    private var flaggedCommentsList = mutableListOf<FlaggedCommentDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flagged_comments)

        recyclerView = findViewById(R.id.recyclerViewFlaggedComments)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchFlaggedComments()
    }

    private fun fetchFlaggedComments() {
        RetrofitClient.adminApiService.getAllFlaggedComments().enqueue(object :
            Callback<List<FlaggedCommentDTO>> {
            override fun onResponse(
                call: Call<List<FlaggedCommentDTO>>,
                response: Response<List<FlaggedCommentDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        flaggedCommentsList.clear()
                        flaggedCommentsList.addAll(it)

                        // Pass 'this@FlaggedCommentsActivity' as the Context
                        adapter = FlaggedCommentsAdapter(this@FlaggedCommentsActivity, flaggedCommentsList)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@FlaggedCommentsActivity, "Failed to load flagged comments", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FlaggedCommentDTO>>, t: Throwable) {
                Toast.makeText(this@FlaggedCommentsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
