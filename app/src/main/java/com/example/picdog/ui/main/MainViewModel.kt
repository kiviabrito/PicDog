package com.example.picdog.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picdog.App
import com.example.picdog.db.AppDatabase
import com.example.picdog.model.ErrorResponse
import com.example.picdog.model.FeedEntity
import com.example.picdog.network.NoConnectivityException
import com.example.picdog.network.PicDogService
import com.example.picdog.utility.GlideCache
import com.example.picdog.utility.SingleLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(
  private val service: PicDogService = App.picDogService,
  private val database: AppDatabase = App.db,
  private val glide: GlideCache = App.glideCache
) : ViewModel() {

  val errorResponse : SingleLiveData<String> = SingleLiveData()

  private val _feedResponse: MutableLiveData<ArrayList<String>> = MutableLiveData()
  val feedResponse: LiveData<ArrayList<String>> = _feedResponse

  var isConnected = true

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
        if (isConnected) {
          requestUpdateFromNetwork(category, feed)
        }
      } catch (e: Exception) {
        postError(e)
      }
    }
  }

  private suspend fun requestUpdateFromNetwork(category: String, feed: FeedEntity?) {
    try {
      val user = database.userDao().selectAll().firstOrNull()
      user?.let { userEntity ->
        val response = service.feedRequest(category, userEntity.token)
        if (response.isSuccessful) {
          if (feed != response.body()) {
            database.feedDao().upsert(response.body()!!)
            postFeed(response.body()!!)
            glide.cachePictures(response.body()!!.list)
          }
        } else {
          val reader = response.errorBody()?.charStream()
          val errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
          this.errorResponse.postValue(errorResponse.error.message)
        }
      }
    } catch (e: Exception) {
      postError(e)
    }
  }

  private fun postFeed(feed: FeedEntity) {
    val photoArray = arrayListOf<String>()
    feed.list.flatMapTo(photoArray) { arrayListOf(it) }
    _feedResponse.postValue(photoArray)
  }

  private fun postError(e: Exception) {
    isConnected = e.message != NoConnectivityException.MESSAGE
    errorResponse.postValue(e.message ?: NoConnectivityException.MESSAGE)
  }

  fun signOut() : Boolean {
    return try {
      viewModelScope.launch(Dispatchers.IO) {
        database.userDao().deleteAll()
        database.feedDao().deleteAll()
        glide.clearCache()
      }
      true
    } catch (e: Exception) {
      errorResponse.postValue(e.message ?: "Unknown")
      false
    }
  }

}