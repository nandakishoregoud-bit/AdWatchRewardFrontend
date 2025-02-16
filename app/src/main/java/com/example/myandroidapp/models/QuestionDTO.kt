package com.example.myandroidapp.models

import java.io.Serializable
import java.time.LocalDateTime

data class QuestionDTO(
    val id: Long?,
    val title: String?,
    val description: String?,
    val category: String?,
    val user: UserDTO?,
    var likes: Int?,
    var dislikes: Int?,
    val createdAt: String?,
    val updatedAt: String?,
    val comments: List<CommentDTO>?
)
