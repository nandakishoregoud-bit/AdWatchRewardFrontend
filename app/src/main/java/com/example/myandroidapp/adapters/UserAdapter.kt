package com.example.myandroidapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.R
import com.example.myandroidapp.models.UserProfileDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserAdapter(private val context: Context, private val userList: List<UserProfileDTO>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.ID)
        val name: TextView = view.findViewById(R.id.userName)
        val email: TextView = view.findViewById(R.id.userEmail)
        val coins: TextView = view.findViewById(R.id.userCoins)
        val adsWatched: TextView = view.findViewById(R.id.adsWatched)
        val status: TextView = view.findViewById(R.id.Status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.id.text = user.id.toString()
        holder.name.text = user.name
        holder.email.text = user.email
        holder.coins.text = "Coins: ${user.coins}"
        holder.adsWatched.text = "Ads Watched: ${user.adsWatched}"
        holder.status.text = if (user.blocked == true) "Blocked" else "Active"

        // Long-press to show block/unblock popup
        holder.itemView.setOnLongClickListener {
            showBlockUnblockDialog(user)
            true
        }
    }

    override fun getItemCount() = userList.size

    private fun showBlockUnblockDialog(user: UserProfileDTO) {
        val action = if (user.blocked == true) "Unblock" else "Block"

        val builder = AlertDialog.Builder(context)
        builder.setTitle("$action User")
            .setMessage("Are you sure you want to $action ${user.name}?")
            .setPositiveButton(action) { _, _ ->
                toggleUserBlockStatus(user)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toggleUserBlockStatus(user: UserProfileDTO) {
        val apiService = if (user.blocked == true) {
            RetrofitClient.adminApiService.unblockUser(user.id)
        } else {
            RetrofitClient.adminApiService.blockUser(user.id)
        }

        apiService.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
