package com.example.myandroidapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.adapters.FlagReasonsAdapter
import com.example.myandroidapp.models.FlagReasonDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlagReasonsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlagReasonsAdapter
    private var flagReasonsList = mutableListOf<FlagReasonDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flag_reasons)

        recyclerView = findViewById(R.id.recyclerViewFlagReasons)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get question ID from intent
        val questionId = intent.getLongExtra("QUESTION_ID", -1)
        if (questionId != -1L) {
            fetchFlagReasons(questionId)
        } else {
            Toast.makeText(this, "Invalid Question ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFlagReasons(questionId: Long) {
        RetrofitClient.adminApiService.getFlagReasonsByQuestionId(questionId).enqueue(object :
            Callback<List<FlagReasonDTO>> {
            override fun onResponse(
                call: Call<List<FlagReasonDTO>>,
                response: Response<List<FlagReasonDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        flagReasonsList.clear()
                        flagReasonsList.addAll(it)
                        adapter = FlagReasonsAdapter(flagReasonsList)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@FlagReasonsActivity, "Failed to load flag reasons", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FlagReasonDTO>>, t: Throwable) {
                Toast.makeText(this@FlagReasonsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
