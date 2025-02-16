package com.example.myandroidapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.R
import com.example.myandroidapp.models.FlagReasonDTO

class CommentFlagReasonAdapter(private val flagReasons: List<FlagReasonDTO>) :
    RecyclerView.Adapter<CommentFlagReasonAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reasonText: TextView = view.findViewById(R.id.tvCommentFlagReason)
        val flaggedByUserId: TextView = view.findViewById(R.id.tvCommentFlaggedById)
        val flaggedByUser: TextView = view.findViewById(R.id.tvCommentFlaggedBy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment_flag_reason, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reason = flagReasons[position]
        holder.reasonText.text = "Reason: ${reason.reason}"
        holder.flaggedByUserId.text = "Flagged By: ${reason.userId}"
        holder.flaggedByUser.text = "Flagged By: ${reason.userName}"
    }

    override fun getItemCount() = flagReasons.size
}
