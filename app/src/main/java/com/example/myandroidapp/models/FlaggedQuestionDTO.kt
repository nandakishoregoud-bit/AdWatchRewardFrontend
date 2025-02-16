package com.example.myandroidapp.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime


data class FlaggedQuestionDTO(
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val likes: Int,
    val dislikes: Int,
    val flagged: Boolean,
    @SerializedName("updatedAt") val updatedAt: String, // Keep as String
    val user: UserDTO?
)



