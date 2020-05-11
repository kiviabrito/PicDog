package com.example.picdog

import android.app.Application
import com.example.picdog.db.AppDatabase
import com.example.picdog.network.PicDogService


class App: Application() {

    companion object {
        lateinit var instance: App

        lateinit var db : AppDatabase

        val picDogService by lazy {
            PicDogService.create()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        db = AppDatabase.getInstance(this)
    }
}