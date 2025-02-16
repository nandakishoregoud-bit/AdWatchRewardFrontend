package com.example.myandroidapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.55.101:8080/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Initialize API services after the retrofit object has been created
    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)
    val adApiService: AdApiService = retrofit.create(AdApiService::class.java)
    val walletApiService: WalletApiService = retrofit.create(WalletApiService::class.java)
    val questionApiService: QuestionApiService = retrofit.create(QuestionApiService::class.java)
    val commentApiService: CommentApiService = retrofit.create(CommentApiService::class.java)
    val adminApiService: AdminApiService = retrofit.create(AdminApiService::class.java)

}


