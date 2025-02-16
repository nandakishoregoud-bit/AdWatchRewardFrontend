package com.example.myandroidapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.R
import com.example.myandroidapp.adapters.UserQuestionAdapter
import com.example.myandroidapp.models.QuestionDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Top10MonthFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserQuestionAdapter
    private val questionsList = mutableListOf<QuestionDTO>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_questions_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewQuestions)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = UserQuestionAdapter(questionsList)
        recyclerView.adapter = adapter
        fetchQuestions()
        return view
    }

    private fun fetchQuestions() {
        RetrofitClient.questionApiService.getTop10OfMonth().enqueue(object : Callback<List<QuestionDTO>> {
            override fun onResponse(call: Call<List<QuestionDTO>>, response: Response<List<QuestionDTO>>) {
                if (response.isSuccessful) {
                    questionsList.clear()
                    response.body()?.let { questionsList.addAll(it) }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Failed to fetch questions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<QuestionDTO>>, t: Throwable) {
                Toast.makeText(context, "Error fetching questions", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
