package com.example.myandroidapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.adapters.FlaggedQuestionsAdapter
import com.example.myandroidapp.models.FlaggedQuestionDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlaggedQuestionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlaggedQuestionsAdapter
    private var flaggedQuestionsList = mutableListOf<FlaggedQuestionDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flagged_questions)

        recyclerView = findViewById(R.id.recyclerViewFlaggedQuestions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchFlaggedQuestions()
    }

    private fun fetchFlaggedQuestions() {
        RetrofitClient.adminApiService.getAllFlaggedQuestions().enqueue(object :
            Callback<List<FlaggedQuestionDTO>> {
            override fun onResponse(
                call: Call<List<FlaggedQuestionDTO>>,
                response: Response<List<FlaggedQuestionDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        flaggedQuestionsList.clear()
                        flaggedQuestionsList.addAll(it)
                        if (!::adapter.isInitialized) {
                            adapter = FlaggedQuestionsAdapter(this@FlaggedQuestionsActivity, flaggedQuestionsList)
                            recyclerView.adapter = adapter
                        } else {
                            adapter.notifyDataSetChanged()  // Ensure the list updates dynamically
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<FlaggedQuestionDTO>>, t: Throwable) {
                Toast.makeText(this@FlaggedQuestionsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
