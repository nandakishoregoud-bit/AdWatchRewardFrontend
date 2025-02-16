package com.example.myandroidapp.network

import com.example.myandroidapp.models.FlagRequest
import com.example.myandroidapp.models.QuestionDTO
import com.example.myandroidapp.models.QuestionDTOForPost
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface QuestionApiService {
    @GET("questions")
    fun getQuestions(@Query("category") category: String?): Call<List<QuestionDTO>>

    @GET("questions/{questionId}")
    fun getQuestionDetails(@Path("questionId") questionId: Long): Call<QuestionDTO>

    // Endpoint to post a new question
    @POST("questions/post/{userId}")
    fun postQuestion(
        @Path("userId") userId: Long,
        @Body questionDTOForPost: QuestionDTOForPost
    ): Call<String>

    @POST("questions/{questionId}/questionlike/{userId}")
    fun likeQuestion(
        @Path("questionId") questionId: Long,
        @Path("userId") userId: Long
    ): Call<String>

    @POST("questions/{questionId}/questiondislike/{userId}")
    fun dislikeQuestion(
        @Path("questionId") questionId: Long,
        @Path("userId") userId: Long
    ): Call<String>

    @POST("flag/{questionId}/question/{userId}")
    fun flagQuestion(
        @Path("questionId") questionId: Long,

        @Path("userId") userId: Long,
        @Body flagRequest: FlagRequest
    ): Call<String>

    @GET("questions/user/{userId}")
    fun getQuestionsByUser(@Path("userId") userId: Long): Call<List<QuestionDTO>>


    @GET("popular/top10ofDay")
    fun getTop10OfDay(): Call<List<QuestionDTO>>

    @GET("popular/top10ofWeek")
    fun getTop10OfWeek(): Call<List<QuestionDTO>>

    @GET("popular/top10ofMonth")
    fun getTop10OfMonth(): Call<List<QuestionDTO>>

    @DELETE("questions/{questionId}/delete/{userId}")
    fun deleteQuestion(
        @Path("questionId") questionId: Long,
        @Path("userId") userId: Long
    ): Call<String>


}