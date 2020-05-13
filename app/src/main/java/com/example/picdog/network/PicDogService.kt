package com.example.picdog.network

import com.example.picdog.model.FeedEntity
import com.example.picdog.model.UserResponse
import retrofit2.Response
import retrofit2.http.*


interface PicDogService {

  @Headers("Content-Type: application/json")
  @POST("signup/")
  suspend fun signupRequest(@Query("email") email: String): Response<UserResponse>

  @Headers("Content-Type: application/json")
  @GET("feed/")
  suspend fun feedRequest(
    @Query("category") category: String,
    @Header("Authorization") token: String
  ): Response<FeedEntity>

}