package com.example.picdog.utility

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.example.picdog.App
import java.io.File

class GlideCache(private val context: Context) {

  fun cachePictures(list: List<String>) {
    list.forEach {
      val future: FutureTarget<File> = Glide.with(App.instance.applicationContext)
        .load(it)
        .downloadOnly(500, 500)
      future.get()
      println("DEBUG: Caching Picture : $it")
    }
  }

  fun clearCache() {
    Glide.get(context).clearDiskCache()
  }
}