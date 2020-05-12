package com.example.picdog.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.example.picdog.App
import com.example.picdog.db.AppDatabase
import com.example.picdog.model.ErrorResponse
import com.example.picdog.model.FeedEntity
import com.example.picdog.network.PicDogService
import com.example.picdog.utility.SingleLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MainViewModel(
  private val service: PicDogService = App.picDogService,
  private val database: AppDatabase = App.db
) : ViewModel() {

  // Handle Error
  val error: SingleLiveData<String> = SingleLiveData()

  // Handle RecyclerView
  private val _feed = MutableLiveData<ArrayList<String>>()
  val feed: LiveData<ArrayList<String>> = _feed

  fun setIndex(index: Int) {
    when (index) {
      1 -> {
        getFeed("husky")
      }
      2 -> {
        getFeed("hound")
      }
      3 -> {
        getFeed("pug")
      }
      4 -> {
        getFeed("labrador")
      }
    }
  }

  private fun getFeed(category: String) {
    viewModelScope.launch(Dispatchers.Default) {
      try {
        val feed = database.feedDao().findByCategory(category)
        feed?.let {
          val photoArray = arrayListOf<String>()
          _feed.postValue(feed.list.flatMapTo(photoArray) { arrayListOf(it) })
        }
        requestFromNetwork(category, feed)
      } catch (e: Exception) {
        error.postValue(e.message)
      }
    }
  }

  private suspend fun requestFromNetwork(category: String, feed: FeedEntity?) {
    try {
      val user = database.userDao().selectAll().firstOrNull()
      user?.let { userEntity ->
        val response = service.feedRequest(category, userEntity.token)
        if (response.isSuccessful) {
          if (feed != response.body()) {
            database.feedDao().upsert(response.body()!!)
            val photoArray = arrayListOf<String>()
            _feed.postValue(response.body()?.list?.flatMapTo(photoArray) { arrayListOf(it) })
            cacheImages(photoArray)
          }
        } else {
          val reader = response.errorBody()?.charStream()
          val errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
          error.postValue(errorResponse.error.message)
        }
      }
    } catch (e: Exception) {
      error.postValue(e.message)
    }
  }

  private fun cacheImages(list: ArrayList<String>){
    list.forEach {
      val future: FutureTarget<File> = Glide.with(App.instance.applicationContext)
        .load(it)
        .downloadOnly(500, 500)
      future.get()
    }
  }

}