package com.example.picdog

import android.app.Application
import com.example.picdog.db.AppDatabase
import com.example.picdog.ui.auth.AuthRepository
import com.example.picdog.api.PicDogService
import com.example.picdog.ui.main.MainRepository
import com.example.picdog.utility.GlideCache


class App : Application() {

  companion object {
    lateinit var instance: App

    lateinit var db: AppDatabase

    val picDogService by lazy {
      PicDogService.create()
    }

    val glideCache by lazy {
      GlideCache(instance.applicationContext)
    }

    val mainRepository by lazy {
      MainRepository()
    }

    val authRepository by lazy {
      AuthRepository()
    }
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    db = AppDatabase.getInstance(this)
  }

}