package com.example.myandroidapp.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.R
import com.example.myandroidapp.models.CommentDTO
import java.time.format.DateTimeFormatter

class CommentAdapter(private val commentList: List<CommentDTO>, private val onDeleteComment: (Long) -> Unit, private val onFlagComment: (Long, Any?) -> Unit) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.userName.text = comment.userName
        holder.commentText.text = comment.text
        holder.updatedAt.text = comment.updatedAt?.let {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm")
            "Updated At: ${it.format(formatter)}"
        } ?: "Updated At: --"

        // Detect long press for 2 seconds (only triggers after long press, not while scrolling)
        holder.itemView.setOnLongClickListener {

                showOptionsDialog(holder.itemView.context, comment.id)

            true
        }
    }

    override fun getItemCount(): Int = commentList.size

    // Show options menu (Flag/Delete)
    private fun showOptionsDialog(context: Context, commentId: Long) {
        val options = arrayOf("Flag", "Delete")
        val alertDialog = android.app.AlertDialog.Builder(context)
            .setTitle("Comment Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showFlagDialog(context, commentId) // Flag comment
                    1 -> showDeleteDialog(context, commentId) // Show delete confirmation
                }
            }
            .create()
        alertDialog.show()
    }

    // Show reason input dialog for flagging
    private fun showFlagDialog(context: Context, commentId: Long) {
        val input = EditText(context)
        input.hint = "Enter reason for flagging"

        val dialog = AlertDialog.Builder(context)
            .setTitle("Flag Comment")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                val reason = input.text.toString().trim()
                if (reason.isNotEmpty()) {
                    onFlagComment(commentId, reason) // Call function from `QuestionDetailsActivity`
                } else {
                    Toast.makeText(context, "Reason cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }



    private fun showDeleteDialog(context: Context, commentId: Long) {
        val alertDialog = android.app.AlertDialog.Builder(context)
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("Delete") { _, _ ->
                onDeleteComment(commentId) // Call delete function
            }
            .setNegativeButton("Cancel", null)
            .create()
        alertDialog.show()
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tv_user_name)
        val commentText: TextView = itemView.findViewById(R.id.tv_comment_text)
        val updatedAt: TextView = itemView.findViewById(R.id.tv_comment_updated_at)
    }
}
