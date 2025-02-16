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
import com.example.myandroidapp.FlagReasonsActivity
import com.example.myandroidapp.R
import com.example.myandroidapp.models.FlaggedQuestionDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlaggedQuestionsAdapter(
    private val context: Context,
    private var flaggedQuestions: MutableList<FlaggedQuestionDTO>
) : RecyclerView.Adapter<FlaggedQuestionsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionTitle: TextView = view.findViewById(R.id.tvQuestionTitle)
        val questionDescription: TextView = view.findViewById(R.id.tvQuestionDescription)
        val questionCategory: TextView = view.findViewById(R.id.tvQuestionCategory)
        val questionOwner: TextView = view.findViewById(R.id.tvQuestionOwner)
        val questionLikes: TextView = view.findViewById(R.id.tvQuestionLikes)
        val questionDislikes: TextView = view.findViewById(R.id.tvQuestionDislikes)
        val questionUpdatedAt: TextView = view.findViewById(R.id.tvQuestionUpdatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flagged_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = flaggedQuestions[position]
        holder.questionTitle.text = question.title
        holder.questionDescription.text = question.description
        holder.questionCategory.text = "Category: ${question.category}"
        holder.questionOwner.text = "Owner: ${question.user?.id}"
        holder.questionLikes.text = "Likes: ${question.likes}"
        holder.questionDislikes.text = "Dislikes: ${question.dislikes}"
        holder.questionUpdatedAt.text = "Updated At: ${question.updatedAt.substring(0, 10)}"

        // Navigate to FlagReasonsActivity on item click
        holder.itemView.setOnClickListener {
            val intent = Intent(context, FlagReasonsActivity::class.java)
            intent.putExtra("QUESTION_ID", question.id) // Pass Question ID
            context.startActivity(intent)
        }

        // Handle long press (Delete or Unflag)
        holder.itemView.setOnLongClickListener {
            showOptionsDialog(question, position)
            true // Indicate long press is handled
        }
    }

    override fun getItemCount() = flaggedQuestions.size

    // Show options for Delete or Unflag
    private fun showOptionsDialog(question: FlaggedQuestionDTO, position: Int) {
        val options = arrayOf("Unflag", "Delete")
        AlertDialog.Builder(context)
            .setTitle("Manage Question")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> unflagQuestion(question.id, position) // Unflag
                    1 -> deleteQuestion(question.id, position) // Delete
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // API Call to Unflag Question
    private fun unflagQuestion(questionId: Long, position: Int) {
        RetrofitClient.adminApiService.unflagQuestion(questionId).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Question unflagged", Toast.LENGTH_SHORT).show()
                    flaggedQuestions.removeAt(position)
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



    // API Call to Delete Question
    private fun deleteQuestion(questionId: Long, position: Int) {
        RetrofitClient.adminApiService.deleteQuestion(questionId).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Question deleted", Toast.LENGTH_SHORT).show()
                    flaggedQuestions.removeAt(position)
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
