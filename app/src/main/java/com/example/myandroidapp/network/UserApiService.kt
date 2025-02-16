package com.example.myandroidapp.network

import android.provider.ContactsContract.CommonDataKinds.Email
import com.example.myandroidapp.models.UserLoginDTO
import com.example.myandroidapp.models.UserLoginResponseDTO
import com.example.myandroidapp.models.UserProfileDTO
import com.example.myandroidapp.models.UserRegistrationDTO
import com.example.myandroidapp.models.VerificationCodeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Define the API endpoints
interface UserApiService {
    @POST("users/register")
    suspend fun registerUser(@Body userRegistrationDTO: UserRegistrationDTO): Response<String>

    @POST("users/login")
    suspend fun loginUser(@Body userLoginDTO: UserLoginDTO): Response<UserLoginResponseDTO>

    @GET("users/{id}")
    suspend fun getUserProfile(@Path("id") userId: Long): Response<UserProfileDTO>

    @POST("users/send-verification-code")
    suspend fun sendVerificationCode(@Query("email") email: String): Response<VerificationCodeResponse>

}


