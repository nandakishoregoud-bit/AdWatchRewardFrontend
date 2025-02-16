package com.example.myandroidapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myandroidapp.R
import com.example.myandroidapp.models.FlagReasonDTO

class FlagReasonsAdapter(private val flagReasons: List<FlagReasonDTO>) :
    RecyclerView.Adapter<FlagReasonsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reasonText: TextView = view.findViewById(R.id.tvFlagReason)
        val userIdText: TextView = view.findViewById(R.id.tvFlaggedById)
        val userNameText: TextView = view.findViewById(R.id.tvFlaggedBy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flag_reason, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flagReason = flagReasons[position]
        holder.reasonText.text = "Reason: ${flagReason.reason}"
        holder.userIdText.text = "Flagged by Id: ${flagReason.userId}"
        holder.userNameText.text = "Flagged by: ${flagReason.userName}"
    }

    override fun getItemCount() = flagReasons.size
}
