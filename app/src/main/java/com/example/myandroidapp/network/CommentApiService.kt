package com.example.myandroidapp.network

import com.example.myandroidapp.models.Comment
import com.example.myandroidapp.models.FlagRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApiService {

    @POST("questions/{questionId}/comment/{userId}")
    fun postComment(
        @Path("questionId") questionId: Long,
        @Path("userId") userId: Long,
        @Body comment: Comment
    ): Call<String>

    @DELETE("questions/{questionId}/comments/{userId}/{commentId}")
    fun deleteComment(
        @Path("questionId") questionId: Long,
        @Path("userId") userId: Long,
        @Path("commentId") commentId: Long
    ): Call<String>

    @POST("flag/{questionId}/comment/{commentId}/{userId}")
    fun flagComment(
        @Path("questionId") questionId: Long,
        @Path("commentId") commentId: Long,
                    @Path("userId") userId: Long,
                    @Body flagRequest: FlagRequest): Call<String>

}
