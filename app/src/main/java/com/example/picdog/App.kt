package com.example.picdog

import android.app.Application
import com.example.picdog.db.AppDatabase
import com.example.picdog.network.PicDogRetrofitBuilder
import com.example.picdog.network.PicDogService
import com.example.picdog.utility.GlideCache


class App : Application() {

  companion object {
    lateinit var instance: App

    lateinit var db: AppDatabase

    val picDogService by lazy {
      PicDogRetrofitBuilder.apiService
    }

    val glideCache by lazy {
      GlideCache(instance.applicationContext)
    }
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    db = AppDatabase.getInstance(this)
  }

}