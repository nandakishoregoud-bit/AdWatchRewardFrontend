package com.example.myandroidapp.network

import com.example.myandroidapp.models.FlagReasonDTO
import com.example.myandroidapp.models.FlaggedCommentDTO
import com.example.myandroidapp.models.FlaggedQuestionDTO
import com.example.myandroidapp.models.UserProfileDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT

interface AdminApiService {

    @GET("admin/users")
    fun getAllUsers(): Call<List<UserProfileDTO>>

    @GET("admin/blocked-users")
    fun getBlockedUsers(): Call<List<UserProfileDTO>>

    @PUT("admin/block-user/{userId}")
    fun blockUser(@Path("userId") userId: Long): Call<String>

    @PUT("admin/unblock-user/{userId}")
    fun unblockUser(@Path("userId") userId: Long): Call<String>

    @GET("admin/flagged-questions")
    fun getAllFlaggedQuestions(): Call<List<FlaggedQuestionDTO>>

    @GET("admin/flagged-questions/{questionId}/reasons")
    fun getFlagReasonsByQuestionId(@Path("questionId") questionId: Long): Call<List<FlagReasonDTO>>

    @DELETE("admin/flagged-questions/{questionId}")
    fun deleteQuestion(@Path("questionId") questionId: Long): Call<Void>

    @GET("admin/flagged-comments")
    fun getAllFlaggedComments(): Call<List<FlaggedCommentDTO>>

    @GET("admin/flagged-comments/{commentId}/reasons")
    fun getFlagReasonsByCommentId(@Path("commentId") commentId: Long): Call<List<FlagReasonDTO>>

    @DELETE("admin/flagged-comments/{commentId}")
    fun deleteComment(@Path("commentId") commentId: Long): Call<Void>

    @POST("admin/flagged-questions/{questionId}/unflag")
    fun unflagQuestion(@Path("questionId") questionId: Long): Call<Void>

    @POST("admin/flagged-comments/{commentId}/unflag")
    fun unflagComment(@Path("commentId") commentId: Long): Call<Void>


}
