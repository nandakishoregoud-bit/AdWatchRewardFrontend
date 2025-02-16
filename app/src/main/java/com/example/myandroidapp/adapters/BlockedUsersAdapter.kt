package com.example.myandroidapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.R
import com.example.myandroidapp.models.UserProfileDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockedUsersAdapter(
    private val context: Context,
    private val blockedUsers: MutableList<UserProfileDTO>
) : RecyclerView.Adapter<BlockedUsersAdapter.BlockedUserViewHolder>() {

    class BlockedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvName)
        val id: TextView = itemView.findViewById(R.id.tvId)
        val email: TextView = itemView.findViewById(R.id.tvEmail)
        val coins: TextView = itemView.findViewById(R.id.tvCoins)
        val adsWatched: TextView = itemView.findViewById(R.id.tvAdsWatched)
        val btnUnblock: Button = itemView.findViewById(R.id.btnUnblock) // Unblock button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedUserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blocked_user, parent, false)
        return BlockedUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockedUserViewHolder, position: Int) {
        val user = blockedUsers[position]
        holder.name.text = user.name
        holder.id.text = user.id.toString()
        holder.email.text = user.email
        holder.coins.text = "Coins: ${user.coins}"
        holder.adsWatched.text = "Ads Watched: ${user.adsWatched}"

        // Handle Unblock Click
        holder.btnUnblock.setOnClickListener {
            showUnblockDialog(user, position)
        }
    }

    override fun getItemCount(): Int = blockedUsers.size

    private fun showUnblockDialog(user: UserProfileDTO, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Unblock User")
            .setMessage("Are you sure you want to unblock ${user.name}?")
            .setPositiveButton("Unblock") { _, _ ->
                unblockUser(user, position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun unblockUser(user: UserProfileDTO, position: Int) {
        RetrofitClient.adminApiService.unblockUser(user.id).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "${user.name} has been unblocked", Toast.LENGTH_SHORT).show()
                    blockedUsers.removeAt(position)
                    notifyItemRemoved(position) // Update RecyclerView after unblocking
                } else {
                    Toast.makeText(context, "Failed to unblock user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
