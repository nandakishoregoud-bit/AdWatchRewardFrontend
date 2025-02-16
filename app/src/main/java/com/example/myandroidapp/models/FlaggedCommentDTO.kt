package com.example.myandroidapp.models

import com.google.gson.annotations.SerializedName

data class FlaggedCommentDTO(
    val commentId: Long,
    val text: String,

    @SerializedName("createdAt")
    val createdAt: String,  // Using String for easy JSON parsing

    val userId: Long,
    val userName: String,
    val reason: String
)
