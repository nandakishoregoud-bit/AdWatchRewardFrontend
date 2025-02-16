package com.example.myandroidapp.network

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface AdApiService {
    @POST("ads/watch")
    suspend fun recordAdWatch(
        @Query("userId") userId: Long,
        @Query("adDuration") adDuration: Int
    ): Response<Void>
}
