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
import com.example.myandroidapp.QuestionDetailsActivity
import com.example.myandroidapp.R
import com.example.myandroidapp.models.QuestionDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserQuestionAdapter(private val questions: MutableList<QuestionDTO>) :
    RecyclerView.Adapter<UserQuestionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_title)
        val category: TextView = view.findViewById(R.id.tv_category)
        val likes: TextView = view.findViewById(R.id.tv_likes)
        val dislikes: TextView = view.findViewById(R.id.tv_dislikes)
        val updatedAt: TextView = view.findViewById(R.id.tv_updated_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        holder.title.text = question.title
        holder.category.text = "Category: ${question.category}"
        holder.likes.text = "Likes: ${question.likes}"
        holder.dislikes.text = "Dislikes: ${question.dislikes}"
        holder.updatedAt.text = "Posted on: ${question.updatedAt}"

        val context = holder.itemView.context

        // ✅ Click to open QuestionDetailsActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, QuestionDetailsActivity::class.java)
            intent.putExtra("questionId", question.id)
            context.startActivity(intent)
        }

        // ✅ Long press to delete
        holder.itemView.setOnLongClickListener {
            question.id?.let { it1 -> showDeleteConfirmationDialog(context, position, it1) }
            true
        }
    }

    override fun getItemCount() = questions.size

    // ✅ Method to update the list dynamically
    fun updateData(newQuestions: List<QuestionDTO>) {
        questions.clear()
        questions.addAll(newQuestions)
        notifyDataSetChanged()
    }

    // ✅ Show confirmation dialog before deleting
    private fun showDeleteConfirmationDialog(context: Context, position: Int, questionId: Long) {
        AlertDialog.Builder(context)
            .setTitle("Delete Question")
            .setMessage("Are you sure you want to delete this question?")
            .setPositiveButton("Delete") { _, _ ->
                deleteQuestion(context, position, questionId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ✅ Get User ID from SharedPreferences
    private fun getUserIdFromPreferences(context: Context): Long {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("USER_ID", -1)
    }

    // ✅ Call API to delete the question
    private fun deleteQuestion(context: Context, position: Int, questionId: Long) {
        val userId = getUserIdFromPreferences(context)
        if (userId == -1L) {
            Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.questionApiService.deleteQuestion(questionId, userId).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    questions.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(context, "Question deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete question", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "Error deleting question", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
