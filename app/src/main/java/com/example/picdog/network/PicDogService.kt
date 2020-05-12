package com.example.picdog.network

import com.example.picdog.App
import com.example.picdog.model.FeedEntity
import com.example.picdog.model.UserEntity
import com.example.picdog.model.UserResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

  companion object {
    fun create(): PicDogService {
      val oktHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetworkConnectionInterceptor(App.instance.applicationContext))

      val retrofit = Retrofit.Builder()
        .addConverterFactory(
          GsonConverterFactory.create()
        )
        .baseUrl("https://iddog-nrizncxqba-uc.a.run.app/")
        .client(oktHttpClient.build())
        .build()
      return retrofit.create(PicDogService::class.java)
    }
  }
}