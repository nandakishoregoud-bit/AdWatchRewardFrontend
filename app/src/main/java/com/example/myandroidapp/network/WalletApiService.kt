package com.example.myandroidapp.network

import com.example.myandroidapp.models.WalletResponse
import retrofit2.Response
import retrofit2.http.*

interface WalletApiService {

    // Get Wallet Balance
    @GET("/api/wallet/{userId}")
    suspend fun getWalletBalance(
        @Path("userId") userId: Long
    ): Response<Double>

    // Redeem Points
    @POST("/api/wallet/le/redeem")
    suspend fun redeemPoints(
        @Query("userId") userId: Long,
        @Query("points") points: Double,
        @Query("rewardType") rewardType: String,
        @Query("upiId") upiId: String? = null,
        @Query("bankAccountNumber") bankAccountNumber: String? = null,
        @Query("bankIFSC") bankIFSC: String? = null,
        @Query("paypalEmail") paypalEmail: String? = null,
        @Query("amazonPayNumber") amazonPayNumber: String? = null
    ): Response<String>
}
