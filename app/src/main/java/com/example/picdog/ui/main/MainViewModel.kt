package com.example.picdog.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.example.picdog.App
import com.example.picdog.db.AppDatabase
import com.example.picdog.model.ErrorResponse
import com.example.picdog.model.FeedEntity
import com.example.picdog.model.FeedResponse
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

  val feedResponse: SingleLiveData<FeedResponse> = SingleLiveData()

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
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val feed = database.feedDao().findByCategory(category)
        feed?.let {
          postFeed(feed)
        }
        requestFromNetwork(category, feed)
      } catch (e: Exception) {
        feedResponse.postValue(FeedResponse.Failure(e.message ?: "Internet Connection"))
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
            postFeed(response.body()!!)
            cacheImages(response.body()!!.list!!)
          }
        } else {
          val reader = response.errorBody()?.charStream()
          val errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
          feedResponse.postValue(FeedResponse.Failure(errorResponse.error.message))
        }
      }
    } catch (e: Exception) {
      feedResponse.postValue(FeedResponse.Failure(e.message ?: "Internet Connection"))
    }
  }


  private fun postFeed(feed: FeedEntity) {
    val photoArray = arrayListOf<String>()
    feed.list.flatMapTo(photoArray) { arrayListOf(it) }
    feedResponse.postValue(FeedResponse.Success(photoArray))
  }

  private fun cacheImages(list: List<String>) {
    list.forEach {
      val future: FutureTarget<File> = Glide.with(App.instance.applicationContext)
        .load(it)
        .downloadOnly(500, 500)
      future.get()
    }
  }

  fun signOut() : Boolean {
    return try {
      viewModelScope.launch(Dispatchers.IO) {
        database.userDao().deleteAll()
        database.feedDao().deleteAll()
        Glide.get(App.instance.applicationContext).clearDiskCache()
      }
      true
    } catch (e: Exception) {
      feedResponse.postValue(FeedResponse.Failure(e.message ?: "Unknown"))
      false
    }
  }

}