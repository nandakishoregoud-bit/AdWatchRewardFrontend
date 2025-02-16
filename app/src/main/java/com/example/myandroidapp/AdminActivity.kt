package com.example.myandroidapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.adapters.UserAdapter
import com.example.myandroidapp.models.UserProfileDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var adapter: UserAdapter
    private var userList = mutableListOf<UserProfileDTO>()
    private var filteredList = mutableListOf<UserProfileDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchUsers()

        // Add search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchUsers() {
        RetrofitClient.adminApiService.getAllUsers().enqueue(object :
            Callback<List<UserProfileDTO>> {
            override fun onResponse(
                call: Call<List<UserProfileDTO>>,
                response: Response<List<UserProfileDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        userList.clear()
                        userList.addAll(it)
                        filteredList.addAll(userList) // Initially, filtered list is the same
                        adapter = UserAdapter(this@AdminActivity, filteredList)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@AdminActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserProfileDTO>>, t: Throwable) {
                Toast.makeText(this@AdminActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterUsers(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(userList) // Show all users if search is empty
        } else {
            // Filter users by ID (convert to string to compare)
            filteredList.addAll(userList.filter { it.id.toString().contains(query) })
        }
        adapter.notifyDataSetChanged()
    }

}
