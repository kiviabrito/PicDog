package com.example.picdog.api

import androidx.lifecycle.LiveData
import com.example.picdog.App
import com.example.picdog.model.FeedEntity
import com.example.picdog.model.UserResponse
import com.example.picdog.utility.GenericApiResponse
import com.example.picdog.utility.LiveDataCallAdapterFactory
import com.example.picdog.utility.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface PicDogService {

  @Headers("Content-Type: application/json")
  @POST("signup/")
  fun signupRequest(@Query("email") email: String): LiveData<GenericApiResponse<UserResponse>>

  @Headers("Content-Type: application/json")
  @GET("feed/")
  fun feedRequest(
    @Query("category") category: String,
    @Header("Authorization") token: String
  ): LiveData<GenericApiResponse<FeedEntity>>

  companion object {
    fun create(): PicDogService {
      val oktHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetworkConnectionInterceptor(App.instance.applicationContext))

      val retrofit = Retrofit.Builder()
        .addConverterFactory(
          GsonConverterFactory.create()
        )
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .baseUrl("https://iddog-nrizncxqba-uc.a.run.app/")
        .client(oktHttpClient.build())
        .build()
      return retrofit.create(PicDogService::class.java)
    }
  }
}