package com.example.myandroidapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.QuestionDetailsActivity
import com.example.myandroidapp.R
import com.example.myandroidapp.models.FlagRequest
import com.example.myandroidapp.models.QuestionDTO
import com.example.myandroidapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter


import java.text.SimpleDateFormat
import java.util.*

class QuestionAdapter(
    private val questions: MutableList<QuestionDTO>,
    private val userId: Long
) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    private val originalQuestions = ArrayList<QuestionDTO>().apply { addAll(questions) }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]

        // Set Title and Category
        holder.title.text = question.title
        holder.category.text = "Category: ${question.category}"

        // Set Likes
        holder.likes.text = "Likes: ${question.likes ?: 0}"

        //set dislikes
        holder.dislikes.text = "Dislikes: ${question.dislikes ?: 0}"

        // Handle Like Button Click
        holder.likes.setOnClickListener {
            likeQuestion(question, holder)
        }

        holder.dislikes.setOnClickListener{
            dislikeQuestion(question, holder)
        }
        // Format and Set UpdatedAt
        val createdAt = question.createdAt
        holder.createdAt.text = if (createdAt != null) {
            val parsedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(createdAt)
            val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(parsedDate!!)
            "Posted At: $formattedDate"
        } else {
            "Posted At: --"
        }

        // Detect long press for 2 seconds (only triggers after long press, not while scrolling)
        holder.itemView.setOnLongClickListener {

            question.id?.let { it1 -> showOptionsDialog(holder.itemView.context, it1) }

            true
        }

        // Set OnClickListener for navigating to QuestionDetailsActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, QuestionDetailsActivity::class.java)
            intent.putExtra("questionId", question.id) // Pass the question ID to the details screen
            context.startActivity(intent)
        }
    }

    // Show options menu (Flag/Delete)
    private fun showOptionsDialog(context: Context, questionId: Long) {
        val options = arrayOf("Flag")
        val alertDialog = android.app.AlertDialog.Builder(context)
            .setTitle("Question Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showFlagDialog(context, questionId) // Flag comment

                }
            }
            .create()
        alertDialog.show()
    }

    // Show reason input dialog for flagging
    private fun showFlagDialog(context: Context, questionId: Long) {
        val input = EditText(context)
        input.hint = "Enter reason for flagging"

        val dialog = AlertDialog.Builder(context)
            .setTitle("Flag Question*")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                val reason = input.text.toString().trim()
                if (reason.isNotEmpty()) {
                    flagQuestion(questionId, reason) // Call function from `QuestionDetailsActivity`
                } else {
                    Toast.makeText(context, "Reason cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }


    private fun flagQuestion(questionId: Long, reason: String) {
        val flagRequest = FlagRequest(reason)

        RetrofitClient.questionApiService.flagQuestion(questionId,userId, flagRequest).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }

    private fun likeQuestion(question: QuestionDTO, holder: QuestionViewHolder) {
        question.id?.let {
            RetrofitClient.questionApiService.likeQuestion(it, userId).enqueue(object :
                Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val message = response.body() ?: "Action completed"
                        Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()

                        // Update UI after like/unlike
                        if (message.contains("removed")) {
                            question.likes = (question.likes ?: 0) - 1
                        } else {
                            question.likes = (question.likes ?: 0) + 1
                        }
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(holder.itemView.context, "Failed to update like", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(holder.itemView.context, "Network Error!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun dislikeQuestion(question: QuestionDTO, holder: QuestionViewHolder) {
        question.id?.let {
            RetrofitClient.questionApiService.dislikeQuestion(it, userId).enqueue(object :
                Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val message = response.body() ?: "Action completed"
                        Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()

                        // Update UI after like/unlike
                        if (message.contains("removed")) {
                            question.dislikes = (question.dislikes ?: 0) - 1
                        } else {
                            question.dislikes = (question.dislikes ?: 0) + 1
                        }
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(holder.itemView.context, "Failed to update like", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(holder.itemView.context, "Network Error!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    fun filter(query: String) {
        val lowerCaseQuery = query.lowercase(Locale.getDefault()).trim()

        val filteredList = if (query.isEmpty()) {
            originalQuestions // ✅ Show all if search is empty
        } else {
            originalQuestions.filter {
                it.title?.lowercase(Locale.getDefault())?.contains(lowerCaseQuery) == true
            }
        }

        questions.clear()
        questions.addAll(filteredList)
        notifyDataSetChanged() // ✅ Refresh UI
    }

    fun updateOriginalList(newList: List<QuestionDTO>) {
        originalQuestions.clear()
        originalQuestions.addAll(newList)
        notifyDataSetChanged() // ✅ Refresh UI
    }

    override fun getItemCount(): Int = questions.size

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val category: TextView = itemView.findViewById(R.id.tv_category)
        val likes: TextView = itemView.findViewById(R.id.tv_likes)
        val dislikes: TextView = itemView.findViewById(R.id.tv_dislikes)
        val createdAt: TextView = itemView.findViewById(R.id.tv_updated_at)
    }
}
