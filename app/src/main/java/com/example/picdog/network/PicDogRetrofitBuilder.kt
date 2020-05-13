package com.example.picdog.network


import com.example.picdog.App
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PicDogRetrofitBuilder {

    private const val BASE_URL: String = "https://iddog-nrizncxqba-uc.a.run.app/"

    private val oktHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor(NetworkConnectionInterceptor(App.instance.applicationContext))

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(oktHttpClient.build())
    }

    val apiService: PicDogService by lazy{
        retrofitBuilder
            .build()
            .create(PicDogService::class.java)
    }
}