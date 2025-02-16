package com.example.myandroidapp.models

data class UserProfileDTO(
    val id: Long,
    val name: String,
    val email: String,
    val coins: Double,
    val adsWatched: Int,
    val referralCode: String?,
    val referredBy: String?, 
    val blocked: Boolean?
)
