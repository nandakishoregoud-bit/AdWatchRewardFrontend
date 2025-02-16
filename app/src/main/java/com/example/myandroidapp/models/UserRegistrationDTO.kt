package com.example.myandroidapp.models

data class UserRegistrationDTO(
    val name: String,
    val email: String,
    val password: String,
    val referralCode: String? = null,
    val referredBy: Long? = null
)

