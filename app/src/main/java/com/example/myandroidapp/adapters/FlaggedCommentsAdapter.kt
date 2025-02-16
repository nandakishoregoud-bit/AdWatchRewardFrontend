package com.example.myandroidapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.CommentFlagReasonsActivity
import com.example.myandroidapp.R
import com.example.myandroidapp.models.FlaggedCommentDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlaggedCommentsAdapter(
    private val context: Context,
    private var flaggedComments: MutableList<FlaggedCommentDTO>
) : RecyclerView.Adapter<FlaggedCommentsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val commentText: TextView = view.findViewById(R.id.tvCommentText)
        val commentUser: TextView = view.findViewById(R.id.tvCommentUser)
        val commentUserName: TextView = view.findViewById(R.id.tvCommentUserName)
        val commentCreatedAt: TextView = view.findViewById(R.id.tvCommentCreatedAt)
        val commentReason: TextView = view.findViewById(R.id.tvCommentReason)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flagged_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = flaggedComments[position]
        holder.commentText.text = comment.text
        holder.commentUser.text = "By: ${comment.userId}"
        holder.commentUserName.text = "By: ${comment.userName}"
        holder.commentCreatedAt.text = "Created At: ${comment.createdAt}"
        holder.commentReason.text = "Reason: ${comment.reason}"

        // Handle click event (Open details)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CommentFlagReasonsActivity::class.java)
            intent.putExtra("COMMENT_ID", comment.commentId) // Pass comment ID
            context.startActivity(intent)
        }

        // Handle long press (Delete or Unflag)
        holder.itemView.setOnLongClickListener {
            showOptionsDialog(comment, position)
            true // Indicate long press is handled
        }
    }

    override fun getItemCount() = flaggedComments.size

    // Show options for Delete or Unflag
    private fun showOptionsDialog(comment: FlaggedCommentDTO, position: Int) {
        val options = arrayOf("Unflag", "Delete")
        AlertDialog.Builder(context)
            .setTitle("Manage Comment")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> unflagComment(comment.commentId, position) // Unflag
                    1 -> showDeleteConfirmationDialog(comment, position) // Show delete confirmation
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    // API Call to Unflag Comment
    private fun unflagComment(commentId: Long, position: Int) {
        RetrofitClient.adminApiService.unflagComment(commentId).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Comment unflagged", Toast.LENGTH_SHORT).show()
                    flaggedComments.removeAt(position)
                    notifyItemRemoved(position)
                } else {
                    Toast.makeText(context, "Failed to unflag", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Show a confirmation dialog before deleting
    private fun showDeleteConfirmationDialog(comment: FlaggedCommentDTO, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("Delete") { _, _ ->
                deleteComment(comment.commentId, position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // API Call to Delete Comment
    private fun deleteComment(commentId: Long, position: Int) {
        RetrofitClient.adminApiService.deleteComment(commentId).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
                    flaggedComments.removeAt(position)
                    notifyItemRemoved(position)
                } else {
                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
