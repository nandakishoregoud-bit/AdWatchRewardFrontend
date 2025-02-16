package com.example.myandroidapp.models

data class CommentDTO(
    val id: Long,
    val text: String,
    val userId: Long,
    val userName: String,
    val updatedAt: String? // Can be null if not updated
)
