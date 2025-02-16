package com.example.myandroidapp.models

data class PaymentDetails(
    val upiId: String? = null,
    val bankAccountNumber: String? = null,
    val bankIFSC: String? = null,
    val paypalEmail: String? = null,
    val amazonPayNumber: String? = null
)
