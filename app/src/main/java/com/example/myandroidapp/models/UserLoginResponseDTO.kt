package com.example.myandroidapp.models

data class UserLoginResponseDTO(
    val email: String,
    val name: String,
    val id: Long,
    val isAdmin: Boolean?
)

