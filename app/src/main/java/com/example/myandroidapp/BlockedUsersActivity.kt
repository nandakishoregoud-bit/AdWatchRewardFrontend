package com.example.myandroidapp.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.R
import com.example.myandroidapp.adapters.BlockedUsersAdapter
import com.example.myandroidapp.models.UserProfileDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockedUsersActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockedUsersAdapter
    private lateinit var searchEditText: EditText
    private val blockedUsersList = mutableListOf<UserProfileDTO>()
    private val filteredList = mutableListOf<UserProfileDTO>() // For search results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blocked_users)

        searchEditText = findViewById(R.id.searchBlockedUsersEditText)
        recyclerView = findViewById(R.id.recyclerViewBlockedUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BlockedUsersAdapter(this, filteredList)
        recyclerView.adapter = adapter

        fetchBlockedUsers()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBlockedUsers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchBlockedUsers() {
        RetrofitClient.adminApiService.getBlockedUsers().enqueue(object :
            Callback<List<UserProfileDTO>> {
            override fun onResponse(
                call: Call<List<UserProfileDTO>>,
                response: Response<List<UserProfileDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        blockedUsersList.clear()
                        blockedUsersList.addAll(it)
                        filteredList.clear()
                        filteredList.addAll(it) // Initially show all blocked users
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<List<UserProfileDTO>>, t: Throwable) {
                Toast.makeText(this@BlockedUsersActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterBlockedUsers(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(blockedUsersList) // Show all users if search is empty
        } else {
            filteredList.addAll(blockedUsersList.filter { it.id.toString().contains(query) })
        }
        adapter.notifyDataSetChanged()
    }
}
